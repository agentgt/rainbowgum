package io.jstach.rainbowgum;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.jstach.rainbowgum.LogEncoder.EncoderProvider;

/**
 * Encoder registry
 */
public sealed interface LogEncoderRegistry extends EncoderProvider {

	/**
	 * Finds an encoder by name.
	 * @param name encoder name.
	 * @return encoder
	 */
	public Optional<LogEncoder> encoder(String name);

	/**
	 * Registers an encoder by name.
	 * @param name encoder name.
	 * @param encoder loaded encoder
	 */
	public void register(String name, LogEncoder encoder);

	/**
	 * Registers an encoder by name.
	 * @param name encoder name.
	 * @param encoder loaded encoder
	 */
	public void register(String name, EncoderProvider encoder);

	/**
	 * Creates encoder registry
	 * @return encoder registry
	 */
	public static LogEncoderRegistry of() {
		return new DefaultEncoderRegistry();
	}

}

final class DefaultEncoderRegistry implements LogEncoderRegistry {

	private Map<String, LogEncoder> encoders = new ConcurrentHashMap<>();

	private Map<String, LogEncoder.EncoderProvider> providers = new ConcurrentHashMap<>();

	@Override
	public Optional<LogEncoder> encoder(String name) {
		return Optional.ofNullable(encoders.get(name));
	}

	@Override
	public void register(String name, LogEncoder encoder) {
		encoders.put(name, encoder);
	}

	@Override
	public LogEncoder provide(URI uri, String name, LogProperties properties) {
		String scheme = uri.getScheme();
		String path = uri.getPath();

		if (scheme == null && path != null) {
			return _encoder(uri, name, path);
		}
		else if (scheme.equals("encoder") || scheme.equals("name")) {
			String _name = uri.getHost();
			if (_name == null) {
				_name = name;
			}
			return _encoder(uri, name, _name);

		}
		var provider = providers.get(scheme);
		if (provider == null) {
			throw new NoSuchElementException("No encoder found. URI=" + uri);
		}
		return provider.provide(uri, name, properties);
	}

	private LogEncoder _encoder(URI uri, String name, String resolvedName) {
		return encoder(resolvedName).orElseThrow(() -> new NoSuchElementException(
				"No encoder found. resolved = " + resolvedName + " name= " + name + ", uri=" + uri));
	}

	@Override
	public void register(String name, EncoderProvider encoder) {
		providers.put(name, encoder);

	}

}
