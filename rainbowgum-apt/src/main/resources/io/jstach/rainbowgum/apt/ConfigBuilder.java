{{=$$ $$=}}
package $$packageName$$;

import io.jstach.rainbowgum.LogProperties;
import io.jstach.rainbowgum.LogProperty;
import io.jstach.rainbowgum.LogProperty.PropertyGetter;
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
	
	$$#properties$$
	$$#normal$$
	final PropertyGetter<$$typeWithNoAnnotation$$> $$propertyVar$$;
	$$/normal$$
	$$/properties$$
	
	$$#properties$$
	$$#normal$$
	private $$fieldType$$ $$name$$ = $$defaultValue$$;
	$$/normal$$
	$$#prefixParameter$$
	private $$typeWithAnnotation$$ $$name$$;
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
		$$#properties$$
		$$#normal$$
		$$propertyVar$$ = Property.builder()
			$$#convertMethod$$$$.$$
			$$/convertMethod$$
			;

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
		var _prefixParameters = _prefixParameters();
		return $$factoryMethod$$(
				$$#properties$$
				$$^-first$$, $$/-first$$$$validate$$
				$$/properties$$
				);
	}
	
	@Override
	public $$builderName$$ fromProperties(LogProperties properties) {
		var __v = LogProperty.Validator.of(this.getClass());
		var _prefixParameters = _prefixParameters();
		$$#properties$$
		$$#normal$$
		var _prop_$$name$$ = $$propertyVar$$.build(LogProperties.interpolateKey($$propertyLiteral$$, _prefixParameters));
		var _$$name$$ = _prop_$$name$$.get(properties).or(this.$$name$$);
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
		var _prefixParameters = _prefixParameters();
		$$#properties$$
		$$#normal$$
		var _$$name$$ = this.$$name$$;
		if (_$$name$$ != null) {
			var _prop = $$propertyVar$$.build(LogProperties.interpolateKey($$propertyLiteral$$, _prefixParameters));
			consumer.accept(_prop.key(), _prop.propertyString(_$$name$$));
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
		return LogProperties.interpolateKey(PROPERTY_PREFIX, _prefixParameters());
	}
	
	private java.util.Map<String,String> _prefixParameters() {
		java.util.Map<String,String> prefixParameters = java.util.Map.of(
				$$#prefixParameters$$
				$$^-first$$, $$/-first$$"$$name$$", $$name$$
				$$/prefixParameters$$
			);
		return prefixParameters;
	}
	
}