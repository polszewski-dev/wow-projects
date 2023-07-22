package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.complex.special.EquivalentAbility;
import wow.commons.model.attributes.complex.special.SpecialAbility;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class EquivalentAbilityMapper extends AbstractMapper<EquivalentAbility> {
	private static final String TYPE_EQUIVALENT = "Equivalent";
	private static final String EQ_STAT = "stat";
	private static final String EQ_AMOUNT = "amount";
	private static final String EQ_LINE = "line";

	protected EquivalentAbilityMapper() {
		super(EquivalentAbility.class, TYPE_EQUIVALENT);
	}

	@Override
	public String toString(EquivalentAbility equivalentAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		putPrimitiveAttributes(EQ_STAT, EQ_AMOUNT, map, equivalentAbility.attributes().getAttributes());
		map.put(EQ_LINE, equivalentAbility.line());

		return SimpleRecordMapper.toString(TYPE_EQUIVALENT, map);
	}

	@Override
	public EquivalentAbility fromString(ParseResult parseResult) {
		var attributes = getPrimitiveAttributes(EQ_STAT, EQ_AMOUNT, parseResult);
		var line = parseResult.getString(EQ_LINE, null);

		return SpecialAbility.equivalent(attributes, line);
	}
}
