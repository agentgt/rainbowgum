package io.jstach.rainbowgum.pattern.format;

import java.net.URI;
import java.time.ZoneId;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.rainbowgum.LogConfig;
import io.jstach.rainbowgum.LogConfig.Provider;
import io.jstach.rainbowgum.LogEncoder;
import io.jstach.rainbowgum.LogProperties;
import io.jstach.rainbowgum.annotation.LogConfigurable;
import io.jstach.rainbowgum.annotation.LogConfigurable.ConvertParameter;
import io.jstach.rainbowgum.annotation.LogConfigurable.KeyParameter;
import io.jstach.rainbowgum.annotation.LogConfigurable.PassThroughParameter;
import io.jstach.rainbowgum.spi.RainbowGumServiceProvider;
import io.jstach.rainbowgum.spi.RainbowGumServiceProvider.Configurator;
import io.jstach.svc.ServiceProvider;

/**
 * Configures Logback style pattern encoders.
 */
@ServiceProvider(RainbowGumServiceProvider.class)
public final class PatternConfigurator implements Configurator {

	/**
	 * For service loader to call.
	 */
	public PatternConfigurator() {
		// for service laoder.
	}

	/**
	 * Pattern encoder uri provider scheme.
	 */
	public static String PATTERN_SCHEME = "pattern";

	@Override
	public boolean configure(LogConfig config) {
		var compiler = compiler(config);
		config.encoderRegistry().register(PATTERN_SCHEME, new PatternEncoderProvider(compiler, config));
		return true;
	}

	static PatternCompiler compiler(LogConfig config) {
		var registry = config.serviceRegistry().putIfAbsent(PatternRegistry.class, () -> PatternRegistry.of());
		var formatter = config.serviceRegistry()
			.putIfAbsent(PatternConfig.class,
					() -> PatternConfig.builder().fromProperties(config.properties()).build());
		var compiler = config.serviceRegistry()
			.putIfAbsent(PatternCompiler.class, () -> new Compiler(registry, formatter));
		return compiler;
	}

	@LogConfigurable(name = "PatternEncoderBuilder", prefix = LogProperties.ENCODER_PREFIX)
	static Provider<LogEncoder> provideEncoder(@KeyParameter String name, String pattern,
			@PassThroughParameter @Nullable PatternCompiler patternCompiler) {
		return (n, config) -> {
			var compiler = patternCompiler;
			if (compiler == null) {
				compiler = PatternConfigurator.compiler(config);
			}
			return LogEncoder.of(compiler.compile(pattern));
		};
	}

	@LogConfigurable(name = "PatternConfigBuilder", prefix = PatternConfig.PATTERN_CONFIG_PREFIX)
	static PatternConfig provideFormatterConfig(@ConvertParameter("convertZoneId") @Nullable ZoneId zoneId,
			@Nullable String lineSeparator, @Nullable Boolean ansiDisabled) {
		PatternConfig dc = PatternConfig.of();
		ansiDisabled = ansiDisabled == null ? dc.ansiDisabled() : ansiDisabled;
		lineSeparator = lineSeparator == null ? dc.lineSeparator() : lineSeparator;
		return new SimpleFormatterConfig(zoneId, lineSeparator, ansiDisabled);

	}

	static ZoneId convertZoneId(@Nullable String zoneId) {
		var dc = PatternConfig.of();
		ZoneId zoneId_ = zoneId == null ? dc.zoneId() : ZoneId.of(zoneId);
		return zoneId_;
	}

	// static Provider<LogFormatter> provideFormatter(@KeyParameter String name, String
	// pattern, String patternRegistry) {
	// return (name_, config) -> {
	// ZoneId zoneId_ = ZoneId.of(zoneId);
	// SimpleFormatterConfig fc = new SimpleFormatterConfig(zoneId_, lineSeparator,
	// ansiDisabled);
	// PatternRegistry registry =
	// config.serviceRegistry().putIfAbsent(PatternRegistry.class, () ->
	// PatternRegistry.of());
	// return new
	// };
	//
	// }
	//
	// private static LogFormatter provideFormatter(String pattern, PatternConfig
	// config, PatternRegistry registry) {
	// var compiler = new Compiler(registry, config);
	// return compiler.compile(pattern);
	// }

}

record PatternEncoderProvider(PatternCompiler compiler, LogConfig config) implements LogEncoder.EncoderProvider {

	@Override
	public LogEncoder provide(URI uri, String name, LogProperties properties) {
		PatternEncoderBuilder b = new PatternEncoderBuilder(name);
		String prefix = b.propertyPrefix();
		LogProperties combined = LogProperties.of(uri, prefix, properties);
		b.fromProperties(combined);
		return b.build().provide(name, config);
	}

}
