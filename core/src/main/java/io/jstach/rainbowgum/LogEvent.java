package io.jstach.rainbowgum;

import java.time.Instant;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.jdt.annotation.Nullable;

public interface LogEvent {

	public static LogEvent of(System.Logger.Level level, String loggerName, String formattedMessage,
			KeyValues keyValues, @Nullable Throwable throwable) {
		Instant timeStamp = Instant.now();
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		long threadId = currentThread.getId();

		return new DefaultLogEvent(timeStamp, threadName, threadId, level, loggerName, formattedMessage, keyValues,
				throwable);
	}

	public static LogEvent of(System.Logger.Level level, String loggerName, String formattedMessage,
			@Nullable Throwable throwable) {
		return of(level, loggerName, formattedMessage, KeyValues.of(), throwable);
	}

	public Instant timeStamp();

	public String threadName();

	public long threadId();

	public System.Logger.Level level();

	public String loggerName();

	public String formattedMessage();

	public Throwable throwable();

	public KeyValues keyValues();

	public interface MessageFormatter {

		void format(StringBuilder builder, String message, Object arg1);

		void format(StringBuilder builder, String message, Object arg1, Object arg2);

		void formatArray(StringBuilder builder, String message, Object[] args);

	}

	public interface EventCreator<LEVEL> extends MessageFormatter {

		public String loggerName();

		public System.Logger.Level translateLevel(LEVEL level);

		public KeyValues keyValues();

		default StringBuilder createMessageBuffer(String message) {
			return new StringBuilder();
		}

		default LogEvent event(LEVEL level, String formattedMessage, @Nullable Throwable throwable) {
			var sysLevel = translateLevel(level);
			var loggerName = loggerName();
			var keyValues = keyValues();
			return LogEvent.of(sysLevel, loggerName, formattedMessage, keyValues, throwable);
		}

		default LogEvent event0(LEVEL level, String formattedMessage) {
			return event(level, formattedMessage, null);
		}

		default LogEvent event1(LEVEL level, String message, Object arg1) {
			// if (arg1 instanceof Throwable t) {
			// return event(level, message, t);
			// }
			StringBuilder sb = createMessageBuffer(message);
			format(sb, message, arg1);
			var formatted = sb.toString();
			return event(level, formatted, null);
		}

		default LogEvent event2(LEVEL level, String message, Object arg1, Object arg2) {
			// if (arg2 instanceof Throwable t) {
			// StringBuilder sb = createMessageBuffer(message);
			// format(sb, message, arg1);
			// var formatted = sb.toString();
			// return event(level, formatted, t);
			// }
			StringBuilder sb = createMessageBuffer(message);
			format(sb, message, arg1, arg2);
			var formatted = sb.toString();
			return event(level, formatted, null);
		}

		default LogEvent eventArray(LEVEL level, String message, Object[] args) {
			// Throwable throwable = getThrowable(args);
			// if (throwable != null) {
			// args = MessageFormatter.trimmedCopy(args);
			// }

			StringBuilder sb = createMessageBuffer(message);
			formatArray(sb, message, args);

			var formatted = sb.toString();
			return event(level, formatted, null);
		}

	}

}

enum EmptyKeyValues implements KeyValues {

	INSTANCE;

	@Override
	public @Nullable String getValue(String key) {
		return null;
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super String> action) {
	}

	@Override
	public <V> int forEach(KeyValuesConsumer<V> action, int index, V storage) {
		return index;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public int next(int index) {
		return -1;
	}

	@Override
	public @Nullable String key(int index) {
		throw new IndexOutOfBoundsException(index);
	}

	@Override
	public @Nullable String value(int index) {
		throw new IndexOutOfBoundsException(index);
	}

	@Override
	public int start() {
		return -1;
	}

	@Override
	public Map<String, String> copyToMap() {
		return Map.of();
	}

}

record DefaultLogEvent(Instant timeStamp, String threadName, long threadId, System.Logger.Level level,
		String loggerName, String formattedMessage, KeyValues keyValues,
		@Nullable Throwable throwable) implements LogEvent {

	public @Nullable Throwable getThrowable() {
		return throwable();
	}

	public KeyValues getKeyValues() {
		return keyValues;
	}

}
