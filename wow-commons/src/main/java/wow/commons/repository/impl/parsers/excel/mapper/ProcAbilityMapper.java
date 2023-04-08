package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.ProcAbility;
import wow.commons.model.attributes.complex.special.ProcEventType;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class ProcAbilityMapper extends AbstractMapper<ProcAbility> {
	private static final String TYPE_PROC = "Proc";
	private static final String P_EVENT = "event";
	private static final String P_CHANCE_PCT = "chance%";
	private static final String P_STAT = "stat";
	private static final String P_AMOUNT = "amount";
	private static final String P_DURATION = "duration";
	private static final String P_COOLDOWN = "cooldown";
	private static final String P_LINE = "line";

	protected ProcAbilityMapper() {
		super(ProcAbility.class, TYPE_PROC);
	}

	@Override
	public String toString(ProcAbility procAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(P_EVENT, procAbility.getEvent().getType());
		map.put(P_CHANCE_PCT, procAbility.getEvent().getChance());
		putPrimitiveAttributes(P_STAT, P_AMOUNT, map, procAbility.getAttributes());
		map.put(P_DURATION, procAbility.getDuration());
		map.put(P_COOLDOWN, procAbility.getCooldown());
		map.put(P_LINE, procAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_PROC, map);
	}

	@Override
	public ProcAbility fromString(ParseResult parseResult) {
		var event = parseResult.getEnum(P_EVENT, ProcEventType::parse);
		Attributes attributes = getPrimitiveAttributes(P_STAT, P_AMOUNT, parseResult);
		var chancePct = parseResult.getPercent(P_CHANCE_PCT);
		var duration = parseResult.getDuration(P_DURATION);
		var cooldown = parseResult.getDuration(P_COOLDOWN, null);
		var line = parseResult.getString(P_LINE, null);

		return SpecialAbility.proc(event, chancePct, attributes, duration, cooldown, line);
	}
}
