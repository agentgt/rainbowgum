package io.jstach.rainbowgum.apt;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheType;

@JStacheConfig(type = JStacheType.STACHE)
@JStache(path = "io/jstach/rainbowgum/apt/ConfigBuilder.java")
record BuilderModel( //
		String builderName, //
		String propertyPrefix, //
		String packageName, //
		String targetType, //
		String factoryMethod, //
		String description, //
		List<PropertyModel> properties) {

	public String nullableAnnotation() {
		return "org.eclipse.jdt.annotation.Nullable";
	}

	public List<PropertyModel> normalProperties() {
		return properties.stream().filter(p -> p.kind == PropertyKind.NORMAL).toList();
	}

	public List<PropertyModel> prefixParameters() {
		return properties.stream().filter(p -> p.kind == PropertyKind.NAME_PARAMETER).toList();
	}

	record PropertyModel(PropertyKind kind, //
			String name, //
			String type, //
			String typeWithAnnotation, //
			String defaultValue, //
			boolean required, //
			String javadoc) {

		private static final String INTEGER_TYPE = "java.lang.Integer";

		private static final String URI_TYPE = "java.net.URI";

		private static final String STRING_TYPE = "java.lang.String";

		public String propertyVar() {
			return "property_" + name;
		}

		public String propertyLiteral() {
			return "PROPERTY_" + name;
		}

		public String convertMethod() {
			return switch (type) {
				case INTEGER_TYPE -> ".toInt()";
				case STRING_TYPE -> "";
				case URI_TYPE -> ".toURI()";
				default -> throw new IllegalStateException(type + " is not supported");
			};
		}

		public String valueMethod() {
			return required ? "value" : "valueOrNull";
		}

		public boolean isNormal() {
			return kind == PropertyKind.NORMAL;
		}

		public boolean isPrefixParameter() {
			return kind == PropertyKind.NAME_PARAMETER;
		}
	}

	enum PropertyKind {

		NORMAL, NAME_PARAMETER

	}

}
