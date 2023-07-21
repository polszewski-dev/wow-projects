package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.ProcEventType;
import wow.commons.model.attributes.complex.special.TalentProcAbility;
import wow.commons.model.spells.EffectId;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class TalentProcAbilityMapper extends AbstractMapper<TalentProcAbility> {
	private static final String TYPE_TALENT_PROC = "TalentProc";
	private static final String TP_EVENT = "event";
	private static final String TP_CHANCE_PCT = "chance%";
	private static final String TP_EFFECT = "effect";
	private static final String TP_DURATION = "duration";
	private static final String TP_STACKS = "stacks";
	private static final String TP_LINE = "line";

	protected TalentProcAbilityMapper() {
		super(TalentProcAbility.class, TYPE_TALENT_PROC);
	}

	@Override
	public String toString(TalentProcAbility talentProcAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(TP_EVENT, talentProcAbility.event().type());
		map.put(TP_CHANCE_PCT, talentProcAbility.event().chance());
		map.put(TP_EFFECT, talentProcAbility.effectId());
		map.put(TP_DURATION, talentProcAbility.duration());
		map.put(TP_STACKS, talentProcAbility.stacks());
		map.put(TP_LINE, talentProcAbility.line());

		return SimpleRecordMapper.toString(TYPE_TALENT_PROC, map);
	}

	@Override
	public TalentProcAbility fromString(ParseResult parseResult) {
		var event = parseResult.getEnum(TP_EVENT, ProcEventType::parse);
		var effectId = parseResult.getEnum(TP_EFFECT, EffectId::parse);
		var chancePct = parseResult.getPercent(TP_CHANCE_PCT, Percent._100);
		var duration = parseResult.getDuration(TP_DURATION, Duration.INFINITE);
		var stacks = parseResult.getInteger(TP_STACKS, 1);
		var line = parseResult.getString(TP_LINE, null);

		return SpecialAbility.talentProc(event, chancePct, effectId, duration, stacks, line);
	}
}
