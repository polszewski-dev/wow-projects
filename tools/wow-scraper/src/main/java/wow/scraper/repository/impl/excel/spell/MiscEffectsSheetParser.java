package wow.scraper.repository.impl.excel.spell;

import wow.commons.model.effect.component.EventAction;
import wow.commons.model.spell.SpellTarget;
import wow.scraper.parser.spell.misc.MiscEffectPattern;
import wow.scraper.parser.spell.params.EffectPatternParams;
import wow.scraper.parser.spell.params.EventParams;
import wow.scraper.repository.impl.SpellPatternRepositoryImpl;

import java.util.Collection;

import static wow.scraper.parser.spell.SpellPatternType.EFFECT;

/**
 * User: POlszewski
 * Date: 2023-09-17
 */
public class MiscEffectsSheetParser extends AbstractSpellPatternSheetParser {
	protected MiscEffectsSheetParser(String sheetName, SpellPatternRepositoryImpl spellPatternRepository) {
		super(sheetName, spellPatternRepository);
	}

	@Override
	protected void readSingleRow() {
		var type = getPatternType(EFFECT);

		switch (type) {
			case EFFECT -> readEffect();
			case TRIGGERED_SPELL -> readTriggeredSpell();
			default -> throw new IllegalArgumentException();
		}
	}

	private void readEffect() {
		var pattern = getPattern();
		var params = getEffectPatternParams();
		var reqVersion = getReqVersion();
		var effect = new MiscEffectPattern(pattern, params, reqVersion);

		validate(effect);

		spellPatternRepository.add(effect);
	}

	private void readTriggeredSpell() {
		var triggeredSpell = getSpellPatternParams();

		getEffect().setTriggeredSpell(0, triggeredSpell);
	}

	private EffectPatternParams getEffect() {
		var pattern = getPattern();
		var reqVersion = getReqVersion();

		return spellPatternRepository.getMiscEffectPattern(pattern, reqVersion)
				.orElseThrow()
				.getParams();
	}

	private void validate(MiscEffectPattern effect) {
		var params = effect.getParams();

		if (params.periodicComponent() != null) {
			throw new IllegalArgumentException("No periodic component allowed");
		}
		if (params.absorptionComponent() != null) {
			throw new IllegalArgumentException("No absorption component allowed");
		}
		if (params.tickInterval() != null) {
			throw new IllegalArgumentException("No timer component allowed");
		}
		if (!"INF".equalsIgnoreCase(params.duration())) {
			throw new IllegalArgumentException("Duration can only be INF");
		}
		if (params.modifierComponent() != null && params.modifierComponent().target() != SpellTarget.SELF) {
			throw new IllegalArgumentException("Modifier target can only be SELF");
		}
		if (params.getEvents().stream().anyMatch(x -> x.target() != SpellTarget.SELF)) {
			throw new IllegalArgumentException("Event target can only be SELF");
		}
		if (hasInvalidEventAction(params)) {
			throw new IllegalArgumentException("Invalid event action");
		}
	}

	private boolean hasInvalidEventAction(EffectPatternParams params) {
		return params.getEvents().stream()
				.map(EventParams::actions)
				.flatMap(Collection::stream)
				.anyMatch(x -> x != EventAction.TRIGGER_SPELL);
	}
}
