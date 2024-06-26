{{=$$ $$=}}
package $$packageName$$;

import io.jstach.rainbowgum.LogProperties;
import io.jstach.rainbowgum.LogProperty;
import io.jstach.rainbowgum.LogProperty.Property;

/**
 * Builder to create {@link $$targetType$$ }.
 $$#descriptionLines$$
 * $$.$$
 $$/descriptionLines$$
 * <table class="table">
 * <caption><strong>Properties retrieved from LogProperties</strong></caption>
 * <tr>
 * <th>Property Pattern</th>
 * <th>Type</th>
 * <th>Required</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 $$#properties$$
 $$#normal$$
 * <tr>
 * <td><code>{@value $$propertyLiteral$$ }</code></td>
 * <td><code>$$typeDescription$$</code></td>
 * <td><code>$$required$$</code></td>
 * <td>$$defaultValueDoc$$</td>
 * <td>$$javadoc$$</td>
 * </tr>
 $$/normal$$ 
 $$/properties$$
 * </table>
 */
public final class $$builderName$$ implements io.jstach.rainbowgum.LogBuilder<$$builderName$$,$$targetType$$> {

	/**
	 * The properties to be retrieved from config will have
	 * this prefix.
	 */
	static final String PROPERTY_PREFIX = "$$propertyPrefix$$";
	
	$$#properties$$
	$$#normal$$
	/**
	 * <code>{@value #$$propertyLiteral$$ } = $$type$$</code> $$javadoc$$
	 */
	static final String $$propertyLiteral$$ = PROPERTY_PREFIX + "$$name$$";
	$$/normal$$
	$$/properties$$
	
	private final String propertyPrefix;
	$$#properties$$
	$$#normal$$
	final Property<$$typeWithNoAnnotation$$> $$propertyVar$$;
	$$/normal$$
	$$/properties$$
	
	$$#properties$$
	$$#normal$$
	private $$fieldType$$ $$name$$ = $$defaultValue$$;
	$$/normal$$
	$$#prefixParameter$$
	private final $$typeWithAnnotation$$ $$name$$;
	$$/prefixParameter$$
	$$#passThrough$$
	private $$typeWithAnnotation$$ $$name$$;
	$$/passThrough$$
	$$/properties$$
	
	/**
	 * Create a builder for {@code $$targetType$$ }.
	 $$#prefixParameters$$
	 * @param $$name$$ will fill <code>$$LB$$$$name$$$$RB$$</code> in <code>$$propertyPrefix$$</code>. $$javadoc$$
	 $$/prefixParameters$$
	 */
	public $$builderName$$(
			$$#prefixParameters$$
			$$^-first$$, $$/-first$$$$type$$ $$name$$
			$$/prefixParameters$$
			) {
		java.util.Map<String,String> prefixParameters = java.util.Map.of(
				$$#prefixParameters$$
				$$^-first$$, $$/-first$$"$$name$$", $$name$$
				$$/prefixParameters$$
			);
		this.propertyPrefix = LogProperties.interpolateKey(PROPERTY_PREFIX, prefixParameters);
		$$#properties$$
		$$#normal$$
		$$propertyVar$$ = Property.builder()
			$$#convertMethod$$$$.$$
			$$/convertMethod$$
			.build(LogProperties.interpolateKey($$propertyLiteral$$, prefixParameters));

		$$/normal$$
		$$#prefixParameter$$
		this.$$name$$ = $$name$$;
		$$/prefixParameter$$
		$$/properties$$
	}

	$$#properties$$
	$$#passThrough$$

	/**
	 * Sets $$#required$$<strong>required</strong> $$/required$$$$name$$.
	 * $$javadoc$$
	 * Default is $$defaultValueDoc$$.
	 * @param $$name$$ not configurable through properties $$javadoc$$
	 * @return this builder.
	 */
	public $$builderName$$ $$name$$($$typeWithAnnotation$$ $$name$$) {
		this.$$name$$ = $$name$$;
		return this;
	}
	$$/passThrough$$
	$$/properties$$

	$$#properties$$
	$$#normal$$
	
	/**
	 * Sets $$#required$$<strong>required</strong> $$/required$$$$name$$.
	 * $$javadoc$$
	 * Default is $$defaultValueDoc$$.
	 * @param $$name$$ <code>{@value #$$propertyLiteral$$ } = $$type$$</code> $$javadoc$$
	 * @return this builder.
	 */
	public $$builderName$$ $$name$$($$typeWithAnnotation$$ $$name$$) {
		this.$$name$$ = $$name$$;
		return this;
	}
	$$/normal$$
	$$/properties$$
	
	/**
	 * Creates {@code $$targetType$$ } from this builder.
	 * @return {@code $$targetType$$ }.
	 $$#exceptions$$
	 * @throws $$.$$ if factory method fails.
	 $$/exceptions$$
	 */
	public $$targetType$$ build() $$throwsList$${
		return $$factoryMethod$$(
				$$#properties$$
				$$^-first$$, $$/-first$$$$#validate$$this.{{name}}$$/validate$$
				$$/properties$$
				);
	}
	
	@Override
	public $$builderName$$ fromProperties(LogProperties properties) {
		var __v = LogProperty.Validator.of(this.getClass());
		$$#properties$$
		$$#normal$$
		var _$$name$$ = $$propertyVar$$.get(properties).or(this.$$name$$);
		__v.$$validateMethod$$(_$$name$$);
		$$/normal$$
		$$/properties$$
		__v.validate();
		$$#properties$$
		$$#normal$$
		this.$$name$$ = _$$name$$.$$valueMethod$$();
		$$/normal$$
		$$/properties$$
		return this;
	}
	
	/**
	 * Turns the builder into java.util.Properties like Map skipping values that are null.
	 * @param consumer apply is called where first arg is key and second is value.
	 */
	public void toProperties(java.util.function.BiConsumer<String, String> consumer) {
		$$#properties$$
		$$#normal$$
		var _$$name$$ = this.$$name$$;
		if (_$$name$$ != null) {
			consumer.accept($$propertyVar$$.key(), $$propertyVar$$.propertyString(_$$name$$));
		}
		$$/normal$$
		$$/properties$$
	}
	
	/**
	 * The interpolated property prefix: {@value #PROPERTY_PREFIX}.
	 * @return resolved prefix which should end with a "<code>.</code>".
	 */
	@Override
	public String propertyPrefix() {
		return this.propertyPrefix;
	}
	
}