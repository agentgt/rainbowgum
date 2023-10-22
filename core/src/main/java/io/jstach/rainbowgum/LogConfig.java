package io.jstach.rainbowgum;

import java.lang.System.Logger.Level;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import io.jstach.rainbowgum.LogConfig.ChangePublisher;
import io.jstach.rainbowgum.LogProperties.Extractor;

public interface LogConfig {

	// default RuntimeMode runtimeMode() {
	// return Property.of(properties(), "mode") //
	// .mapString(s -> s.toUpperCase(Locale.ROOT)) //
	// .map(RuntimeMode::valueOf) //
	// .requireElse(RuntimeMode.DEV);
	// }
	//
	// public enum RuntimeMode {
	//
	// DEV, TEST, PROD
	//
	// }

	public LogProperties properties();

	public LevelResolver levelResolver();

	public Defaults defaults();

	default LogOutputProvider outputProvider() {
		return LogOutputProvider.of();
	}

	public static LogConfig of() {
		return LogConfig.of(LogProperties.StandardProperties.SYSTEM_PROPERTIES);
	}

	public static LogConfig of(LogProperties properties) {
		return new DefaultLogConfig(properties);
	}

	default Optional<ChangePublisher> publisher(String loggerName) {
		return Optional.empty();
	}

	interface ChangePublisher {

		public void subscribe(Consumer<? super LogConfig> consumer);

		public void publish();

	}

}

abstract class AbstractChangePublisher implements ChangePublisher {

	private final Collection<Consumer<? super LogConfig>> consumers = new CopyOnWriteArrayList<Consumer<? super LogConfig>>();

	protected abstract LogConfig _config();

	@Override
	public void publish() {
		LogConfig config = _config();
		for (var c : consumers) {
			c.accept(config);
		}
	}

	@Override
	public void subscribe(Consumer<? super LogConfig> consumer) {
		consumers.add(consumer);
	}

}

class DefaultLogConfig implements LogConfig, ConfigLevelResolver {

	private final LogProperties properties;

	private final ConcurrentHashMap<String, Level> levelCache = new ConcurrentHashMap<>();

	private final Defaults defaults;

	private final ChangePublisher publisher;

	public DefaultLogConfig(LogProperties properties) {
		super();
		this.properties = properties;
		this.defaults = new Defaults(this.properties);
		this.publisher = new AbstractChangePublisher() {
			@Override
			protected LogConfig _config() {
				return DefaultLogConfig.this;
			}
		};
	}

	@Override
	public LogProperties properties() {
		return properties;
	}

	@Override
	public LevelResolver levelResolver() {
		return this;
	}

	@Override
	public Defaults defaults() {
		return defaults;
	}

	@Override
	public Level resolveLevel(String name) {
		var level = levelCache.get(name);
		if (level != null) {
			return level;
		}
		return levelCache.computeIfAbsent(name, n -> ConfigLevelResolver.super.resolveLevel(name));
	}

	private static final Extractor<Boolean> changeSetting = Extractor.of()
		.withSearch("change")
		.map(s -> Boolean.parseBoolean(s))
		.orElse(false);

	@Override
	public Optional<ChangePublisher> publisher(String loggerName) {
		boolean changeAllowed = changeSetting.require(properties(), loggerName);
		if (changeAllowed) {
			return Optional.of(publisher);
		}
		return Optional.empty();
	}

}
