package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.OnUseAbility;
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
		putPrimitiveAttributes(OU_STAT, OU_AMOUNT, map, onUseAbility.getAttributes());
		map.put(OU_DURATION, onUseAbility.getDuration());
		map.put(OU_COOLDOWN, onUseAbility.getCooldown());
		map.put(OU_LINE, onUseAbility.getLine());

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