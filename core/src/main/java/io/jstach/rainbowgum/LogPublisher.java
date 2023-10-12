package io.jstach.rainbowgum;

import java.util.List;

import io.jstach.rainbowgum.LogAppender.ThreadSafeLogAppender;
import io.jstach.rainbowgum.publisher.BlockingQueueAsyncLogPublisher;

public sealed interface LogPublisher extends LogEventLogger, AutoCloseable {

	public void start(LogConfig config);

	public boolean synchronous();

	public void close();

	public interface PublisherProvider {

		LogPublisher provide(LogConfig config, List<? extends LogAppender> appenders);

		public static AsyncLogPublisher.Builder async() {
			return AsyncLogPublisher.builder();
		}

		public static SyncLogPublisher.Builder sync() {
			return SyncLogPublisher.builder();
		}

	}

	abstract class AbstractBuilder<T> {

		protected AbstractBuilder() {
			super();
		}

		protected abstract T self();

		public abstract PublisherProvider build();

	}

	non-sealed interface AsyncLogPublisher extends LogPublisher {

		@Override
		public void start(LogConfig config);

		@Override
		default boolean synchronous() {
			return false;
		}

		public static AsyncLogPublisher.Builder builder() {
			return new Builder();
		}

		public static class Builder extends AbstractBuilder<AsyncLogPublisher.Builder> {

			private int bufferSize = 1024;

			private Builder() {
			}

			public AsyncLogPublisher.Builder bufferSize(int bufferSize) {
				this.bufferSize = bufferSize;
				return this;
			}

			public PublisherProvider build() {
				return (config, appenders) -> BlockingQueueAsyncLogPublisher.of(appenders, bufferSize);
			}

			@Override
			protected AsyncLogPublisher.Builder self() {
				return this;
			}

		}

	}

	non-sealed interface SyncLogPublisher extends LogPublisher {

		public static SyncLogPublisher.Builder builder() {
			return new Builder();
		}

		@Override
		default void start(LogConfig config) {

		}

		default boolean synchronous() {
			return true;
		}

		public static class Builder extends AbstractBuilder<SyncLogPublisher.Builder> {

			private Builder() {
			}

			@Override
			protected SyncLogPublisher.Builder self() {
				return this;
			}

			public PublisherProvider build() {
				return (config, appenders) -> new DefaultSyncLogPublisher(LogAppender.of(appenders));
			}

		}

	}

}

final class DefaultSyncLogPublisher implements LogPublisher.SyncLogPublisher {

	private final ThreadSafeLogAppender appender;

	public DefaultSyncLogPublisher(LogAppender appender) {
		super();
		this.appender = ThreadSafeLogAppender.of(appender);
	}

	@Override
	public void log(LogEvent event) {
		appender.append(event);
	}

	@Override
	public void close() {
		appender.close();
	}

}