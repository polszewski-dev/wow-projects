package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public final class ComplexAttributeMapper {
	private static final Mapper<?>[] MAPPERS = {
			new EquivalentAbilityMapper(),
			new OnUseAbilityMapper(),
			new ProcAbilityMapper(),
			new TalentProcAbilityMapper(),
			new MiscAbilityMapper(),
			new StatConversionMapper(),
			new EffectIncreasePerEffectOnTargetMapper(),
	};

	public static String toString(ComplexAttribute attribute) {
		if (attribute == null) {
			return null;
		}

		assertNoCondition(attribute);

		for (var mapper : MAPPERS) {
			if (mapper.accepts(attribute.getClass())) {
				return ((Mapper)mapper).toString(attribute);
			}
		}

		throw new IllegalArgumentException("Unhandled type: " + attribute.getClass());
	}

	public static ComplexAttribute fromString(String value) {
		ParseResult parseResult = SimpleRecordMapper.fromString(value);

		if (parseResult.getType() == null) {
			return null;
		}

		for (var mapper : MAPPERS) {
			if (mapper.accepts(parseResult.getType())) {
				return mapper.fromString(parseResult);
			}
		}

		throw new IllegalArgumentException("Unhandled type: " + parseResult.getType());
	}

	public static String getIdAndCondition(PrimitiveAttribute attribute) {
		if (!attribute.hasCondition()) {
			return attribute.getId().toString();
		}
		return attribute.getId() + "," + attribute.getCondition();
	}

	private static void assertNoCondition(ComplexAttribute attribute) {
		if (attribute.hasCondition()) {
			throw new IllegalArgumentException("Can't handle conditions: " + attribute);
		}
	}

	private ComplexAttributeMapper() {}
}
