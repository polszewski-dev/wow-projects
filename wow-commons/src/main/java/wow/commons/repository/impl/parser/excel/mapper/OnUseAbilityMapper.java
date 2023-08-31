package wow.commons.repository.impl.parser.excel.mapper;

import wow.commons.model.attribute.complex.special.OnUseAbility;
import wow.commons.model.attribute.complex.special.SpecialAbility;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class OnUseAbilityMapper extends AbstractMapper<OnUseAbility> {
	private static final String TYPE_ON_USE = "OnUse";
	private static final String OU_STAT = "stat";
	private static final String OU_AMOUNT = "amount";
	private static final String OU_DURATION = "duration";
	private static final String OU_COOLDOWN = "cooldown";
	private static final String OU_LINE = "line";

	protected OnUseAbilityMapper() {
		super(OnUseAbility.class, TYPE_ON_USE);
	}

	@Override
	public String toString(OnUseAbility onUseAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		putPrimitiveAttributes(OU_STAT, OU_AMOUNT, map, onUseAbility.attributes());
		map.put(OU_DURATION, onUseAbility.duration());
		map.put(OU_COOLDOWN, onUseAbility.cooldown());
		map.put(OU_LINE, onUseAbility.line());

		return SimpleRecordMapper.toString(TYPE_ON_USE, map);
	}

	@Override
	public OnUseAbility fromString(ParseResult parseResult) {
		var attributes = getPrimitiveAttributes(OU_STAT, OU_AMOUNT, parseResult);
		var duration = parseResult.getDuration(OU_DURATION);
		var cooldown = parseResult.getDuration(OU_COOLDOWN);
		var line = parseResult.getString(OU_LINE, null);

		return SpecialAbility.onUse(attributes, duration, cooldown, line);
	}
}
