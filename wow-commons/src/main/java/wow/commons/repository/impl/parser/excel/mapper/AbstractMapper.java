package wow.commons.repository.impl.parser.excel.mapper;

import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.commons.util.parser.simple.ParseResult;

import java.util.List;
import java.util.Map;

import static wow.commons.repository.impl.parser.excel.mapper.ComplexAttributeMapper.getIdAndCondition;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
abstract class AbstractMapper<T extends ComplexAttribute> implements Mapper<T> {
	private final Class<T> attributeClass;
	private final String typeName;

	protected AbstractMapper(Class<T> attributeClass, String typeName) {
		this.attributeClass = attributeClass;
		this.typeName = typeName;
	}

	@Override
	public <C> boolean accepts(Class<C> attributeClass) {
		return attributeClass == this.attributeClass;
	}

	@Override
	public boolean accepts(String typeName) {
		return typeName.equals(this.typeName);
	}

	protected void putPrimitiveAttributes(String statKey, String amountKey, Map<String, Object> map, Attributes attributes) {
		List<PrimitiveAttribute> primitiveAttributes = attributes.getPrimitiveAttributes();

		assertNumberOfAttributesIsWithinLimit(primitiveAttributes);

		if (primitiveAttributes.size() == 1) {
			putPrimitiveAttribute(statKey, amountKey, primitiveAttributes.get(0), map);
			return;
		}

		int i = 1;
		for (PrimitiveAttribute attribute : primitiveAttributes) {
			putPrimitiveAttribute(statKey + i, amountKey + i, attribute, map);
			++i;
		}
	}

	protected void putPrimitiveAttribute(String statKey, String amountKey, PrimitiveAttribute attribute, Map<String, Object> map) {
		map.put(statKey, getIdAndCondition(attribute));
		map.put(amountKey, attribute.getDouble());
	}

	protected Attributes getPrimitiveAttributes(String statKey, String amountKey, ParseResult parseResult) {
		AttributesBuilder builder = new AttributesBuilder();

		getPrimitiveAttribute(statKey, amountKey, builder, parseResult);

		for (int i = 1; i <= MAX_ATTRIBUTES; ++i) {
			getPrimitiveAttribute(statKey +i, amountKey +i, builder, parseResult);
		}

		return builder.toAttributes();
	}

	protected void getPrimitiveAttribute(String statKey, String amountKey, AttributesBuilder builder, ParseResult parseResult) {
		var statStr = parseResult.getString(statKey, null);

		if (statStr == null) {
			return;
		}

		var attributeSupplier = PrimitiveAttributeSupplier.fromString(statStr);
		var amount = parseResult.getDouble(amountKey, null);
		attributeSupplier.addAttributeList(builder, amount);
	}

	private static final int MAX_ATTRIBUTES = 9;

	protected void assertNumberOfAttributesIsWithinLimit(List<PrimitiveAttribute> attributes) {
		if (attributes.size() > MAX_ATTRIBUTES) {
			throw new IllegalArgumentException(String.format("Can only handle %s instead of %s", MAX_ATTRIBUTES, attributes.size()));
		}
	}
}
