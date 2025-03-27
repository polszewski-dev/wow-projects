package wow.scraper.repository.impl.excel.spell;

import wow.scraper.parser.spell.SpellPatternType;
import wow.scraper.parser.spell.params.EffectPatternParams;
import wow.scraper.parser.spell.proc.ProcPattern;

import java.util.List;
import java.util.TreeMap;

import static wow.scraper.parser.spell.SpellPatternType.TRIGGER_1;

/**
 * User: POlszewski
 * Date: 2023-09-08
 */
public class ProcSheetParser extends AbstractSpellPatternSheetParser {
	public ProcSheetParser(String sheetName, SpellPatternExcelParser parser) {
		super(sheetName, parser);
	}

	@Override
	protected void readSingleRow() {
		var type = getPatternType(TRIGGER_1);

		switch (type) {
			case TRIGGER_1, TRIGGER_2, TRIGGER_3, TRIGGER_4, TRIGGER_5 -> readTrigger(type);
			case TRIGGERED_SPELL_1, TRIGGERED_SPELL_2, TRIGGERED_SPELL_3, TRIGGERED_SPELL_4, TRIGGERED_SPELL_5 -> readTriggeredSpell(type);
			default -> throw new IllegalArgumentException(type + "");
		}
	}

	private void readTrigger(SpellPatternType type) {
		var triggerEffect = getTriggerEffect();

		var trigger = getEventParams(TRIGGER_PREFIX);
		var triggeredSpell = getSpellPatternParams();

		triggerEffect.setEvent(type.getTriggerIdx(), trigger);
		triggerEffect.setTriggeredSpell(type.getTriggerIdx(), triggeredSpell);
	}

	private void readTriggeredSpell(SpellPatternType type) {
		var triggeredSpell = getSpellPatternParams();

		getTriggerEffect()
				.getTriggeredSpell(type.getTriggerIdx())
				.effectApplication()
				.effect()
				.setTriggeredSpell(type.getTriggerIdx(), triggeredSpell);
	}

	private EffectPatternParams getTriggerEffect() {
		var pattern = getPattern();
		var reqVersion = getReqVersion();
		var procPattern = parser.getProcPattern(pattern, reqVersion).orElse(null);

		if (procPattern == null) {
			var params = new EffectPatternParams(List.of(), null, null, null, List.of(), new TreeMap<>(), null, null);
			procPattern = new ProcPattern(pattern, params, reqVersion);
			parser.add(procPattern);
		}

		return procPattern.getParams();
	}
}
