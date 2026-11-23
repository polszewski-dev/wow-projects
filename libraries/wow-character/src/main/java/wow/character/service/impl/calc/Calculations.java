package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.character.PetCharacter;
import wow.character.model.snapshot.AccumulatedPartialStats;

import static java.lang.Math.max;
import static wow.character.model.snapshot.AttributePredicates.OWNER_OR_AURAS;
import static wow.character.model.snapshot.AttributePredicates.PET_ONLY;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public abstract class Calculations {
	static double addAndMultiplyByPct(double value, double modifier, double modifierPct) {
		return max(value + modifier, 0) * max(1 + modifierPct / 100, 0);
	}

	static double multiplyByRatio(double value, double ratioPct, double increasePct) {
		return value * max(ratioPct / 100.0, 0) * max(1 + increasePct / 100.0, 0);
	}

	static void accumulateEffects(Character character, AccumulatedPartialStats stats) {
		accumulateEffects(character, stats, true);
	}

	static DefaultEffectCollector accumulateEffects(Character character, AccumulatedPartialStats stats, boolean performConversions) {
		var collector = new DefaultEffectCollector(character, OWNER_OR_AURAS, stats);

		collector.solveAll();

		if (character instanceof PetCharacter petCharacter) {
			var masterCollector = new DefaultEffectCollector(petCharacter.getMaster(), PET_ONLY, stats);

			masterCollector.solveAll();
		}

		if (performConversions) {
			collector.solveStatConversions();
		}

		return collector;
	}
}
