package io.jstach.rainbowgum;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.jstach.rainbowgum.LogOutput.OutputProvider;

/**
 * Register output providers by URI scheme.
 */
public sealed interface LogOutputRegistry extends OutputProvider permits DefaultOutputRegistry {

	/**
	 * A meta URI scheme to reference outputs registered somewhere else.
	 */
	public static String NAMED_OUTPUT_SCHEME = "output";

	/**
	 * Register a provider by {@link URI#getScheme() scheme}.
	 * @param scheme URI scheme to match for.
	 * @param provider provider for scheme.
	 */
	public void register(String scheme, OutputProvider provider);

	/**
	 * Finds an output by name.
	 * @param name output name.
	 * @return maybe an output.
	 */
	Optional<LogOutput> output(String name);

	/**
	 * Register a provider by {@link URI#getScheme() scheme}.
	 * @param name URI scheme to match for.
	 * @param output for name.
	 */
	public void register(String name, LogOutput output);

	/**
	 * Default output provider.
	 * @return default output provider.
	 */
	public static LogOutputRegistry of() {
		return new DefaultOutputRegistry();
	}

}

final class DefaultOutputRegistry implements LogOutputRegistry {

	private final Map<String, OutputProvider> providers = new ConcurrentHashMap<>();

	private final Map<String, LogOutput> outputs = new ConcurrentHashMap<>();

	@Override
	public void register(String scheme, OutputProvider provider) {
		providers.put(scheme, provider);
	}

	@Override
	public void register(String name, LogOutput output) {
		outputs.put(name, output);
	}

	@Override
	public Optional<LogOutput> output(String name) {
		return Optional.ofNullable(outputs.get(name));
	}

	LogOutput provide(String name, LogProperties properties) throws IOException {
		var o = output(name).orElse(null);
		if (o != null) {
			return o;
		}
		return output(URI.create(name + ":///"), name, properties);
	}

	@Override
	public LogOutput output(URI uri, String name, LogProperties properties) throws IOException {
		String scheme = uri.getScheme();
		String path = uri.getPath();
		OutputProvider customProvider;
		if (scheme == null && path != null) {
			if (name.equals(LogAppender.FILE_APPENDER_NAME)) {
				@SuppressWarnings("resource")
				FileOutputStream fos = new FileOutputStream(path);
				return LogOutput.of(uri, fos.getChannel());
			}
			else {
				return provide(name, properties);
			}
		}
		else if (NAMED_OUTPUT_SCHEME.equals(scheme)) {
			String host = uri.getHost();
			if (host != null) {
				name = host;
			}
			String _name = name;
			return output(_name).orElseThrow(() -> new IOException("Output for name: " + _name + " not found."));
		}
		else if (LogOutput.STDOUT_SCHEME.equals(scheme)) {
			return LogOutput.ofStandardOut();
		}
		else if (LogOutput.STDERR_SCHEME.equals(scheme)) {
			return LogOutput.ofStandardErr();
		}
		else if ((customProvider = providers.get(scheme)) != null) {
			return customProvider.output(uri, name, properties);
		}
		else {
			var p = Paths.get(uri);
			var channel = FileChannel.open(p, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			channel.close();
			return LogOutput.of(uri, channel);
		}
	}

}
