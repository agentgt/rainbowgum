package io.jstach.rainbowgum.slf4j;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import io.jstach.rainbowgum.LogConfig.ChangePublisher.ChangeType;
import io.jstach.rainbowgum.LogEventLogger;
import io.jstach.rainbowgum.RainbowGum;
import io.jstach.rainbowgum.slf4j.spi.LoggerDecoratorService;
import io.jstach.rainbowgum.slf4j.spi.LoggerDecoratorService.DepthAware;

class RainbowGumLoggerFactory implements ILoggerFactory {

	private final ConcurrentMap<String, Logger> loggerMap;

	private final RainbowGum rainbowGum;

	private final LoggerDecorator decorator;

	private final RainbowGumMDCAdapter mdc;

	public RainbowGumLoggerFactory(RainbowGum rainbowGum, RainbowGumMDCAdapter mdc) {
		super();
		this.loggerMap = new ConcurrentHashMap<>();
		this.rainbowGum = rainbowGum;
		this.decorator = LoggerDecorator.of(rainbowGum);
		this.mdc = mdc;
	}

	@Override
	public Logger getLogger(String name) {
		Logger simpleLogger = loggerMap.get(name);
		if (simpleLogger != null) {
			return simpleLogger;
		}
		else {
			var router = this.rainbowGum.router();
			var changePublisher = this.rainbowGum.config().changePublisher();

			Logger newLogger;
			var level = router.levelResolver().resolveLevel(name);
			var allowedChanges = changePublisher.allowedChanges(name);
			if (!allowedChanges.isEmpty()) {
				/*
				 * We get a logger that can log everything.
				 */
				LogEventLogger logger = router.route(name, System.Logger.Level.ERROR);
				boolean callerInfo = allowedChanges.contains(ChangeType.CALLER);
				ChangeableLogger changeable = new ChangeableLogger(name, logger, mdc, Levels.toSlf4jInt(level),
						callerInfo);
				changePublisher.subscribe(c -> {
					var slf4jLevel = Levels.toSlf4jLevel(router.levelResolver().resolveLevel(name));
					changeable.setLevel(slf4jLevel.toInt());
				});
				newLogger = changeable;
			}
			else {
				LogEventLogger logger = router.route(name, level);
				if (level == System.Logger.Level.OFF) {
					newLogger = new LevelLogger.OffLogger(name);
				}
				else {
					var slf4jLevel = Levels.toSlf4jLevel(level);
					newLogger = LevelLogger.of(slf4jLevel, name, logger, mdc);
				}
			}
			Logger decorated = decorator.decorate(rainbowGum, newLogger);
			Logger oldInstance = loggerMap.putIfAbsent(name, decorated);
			return oldInstance == null ? decorated : oldInstance;
		}
	}

	sealed interface LoggerDecorator {

		public Logger decorate(RainbowGum gum, Logger logger);

		public static LoggerDecorator of(RainbowGum gum) {
			var array = gum.config()
				.serviceRegistry()
				.find(LoggerDecoratorService.class)
				.toArray(i -> new LoggerDecoratorService[i]);
			Arrays.sort(array,
					Comparator.comparingInt(LoggerDecoratorService::order).thenComparing(LoggerDecoratorService::name));
			if (array.length == 0) {
				return Noop.INSTANCE;
			}
			return new CompositeLoggerDecorator(array);
		}

		enum Noop implements LoggerDecorator {

			INSTANCE;

			@Override
			public Logger decorate(RainbowGum gum, Logger logger) {
				return logger;
			}

		}

		record CompositeLoggerDecorator(LoggerDecoratorService[] services) implements LoggerDecorator {

			@Override
			public Logger decorate(RainbowGum gum, Logger logger) {
				for (var p : services) {
					logger = Objects.requireNonNull(p.decorate(gum, logger));
				}
				int count = 0;
				for (var current = logger; current != null; current = getWrapping(current)) {
					count++;
				}
				int endIndex = count - 1;
				int i = 0;
				if (endIndex > 0) {
					for (var current = logger; current != null; current = getWrapping(current)) {
						setDepth(current, i, endIndex);
						if (i >= endIndex) {
							break;
						}
						i++;
					}
				}
				return logger;
			}

		}

		private static @Nullable Logger getWrapping(Logger logger) {
			if (logger instanceof WrappingLogger wl) {
				return wl.delegate();
			}
			return null;
		}

		static void setDepth(Logger logger, int index, int endIndex) {
			if (logger instanceof DepthAware da) {
				da.setDepth(index, endIndex);
			}

		}

	}

}
