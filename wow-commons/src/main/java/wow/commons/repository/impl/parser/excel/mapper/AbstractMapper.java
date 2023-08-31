package wow.commons.repository.impl.parser.excel.mapper;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.repository.SpellRepository;
import wow.commons.util.parser.simple.ParseResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
abstract class AbstractMapper implements Mapper {
	protected final SpellRepository spellRepository;

	protected AbstractMapper(SpellRepository spellRepository) {
		this.spellRepository = spellRepository;
	}

	protected void putPrimitiveAttributes(Map<String, Object> map, Attributes attributes) {
		var list = attributes.primitiveAttributes();

		assertNumberOfAttributesIsWithinLimit(list);

		for (int i = 0; i < list.size(); ++i) {
			putPrimitiveAttribute(list.get(i), i + 1, map);
		}
	}

	private void putPrimitiveAttribute(PrimitiveAttribute attribute, int idx, Map<String, Object> map) {
		map.put(getAttrId(idx), attribute.id());
		map.put(getAttrValue(idx), attribute.value());
		map.put(getAttrCondition(idx), attribute.condition());
		if (attribute.levelScaled()) {
			throw new IllegalArgumentException();
		}
	}

	protected Attributes getPrimitiveAttributes(ParseResult parseResult) {
		var list = IntStream.rangeClosed(1, MAX_ATTRIBUTES)
				.mapToObj(idx -> getPrimitiveAttribute(idx, parseResult))
				.filter(Objects::nonNull)
				.toList();
		return Attributes.of(list);
	}

	private PrimitiveAttribute getPrimitiveAttribute(int idx, ParseResult parseResult) {
		var statStr = parseResult.getString(getAttrId(idx), null);

		if (statStr == null) {
			return null;
		}

		var id = parseResult.getEnum(getAttrId(idx), PrimitiveAttributeId::parse);
		var value = parseResult.getDouble(getAttrValue(idx));
		var condition = parseResult.getEnum(getAttrCondition(idx), AttributeCondition::parse, AttributeCondition.EMPTY);

		return Attribute.of(id, value, condition);
	}

	private static final int MAX_ATTRIBUTES = 9;

	protected void assertNumberOfAttributesIsWithinLimit(List<PrimitiveAttribute> attributes) {
		if (attributes.size() > MAX_ATTRIBUTES) {
			throw new IllegalArgumentException(String.format("Can only handle %s instead of %s", MAX_ATTRIBUTES, attributes.size()));
		}
	}
}
