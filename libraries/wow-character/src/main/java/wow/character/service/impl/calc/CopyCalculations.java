package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;

import static wow.character.service.impl.calc.ComponentCalculations.getAccumulatedTargetStats;
import static wow.commons.model.attribute.AttributeId.COPY_PCT;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class CopyCalculations extends Calculations {
	public static double getCopiedAmountAsDamage(Character caster, Spell spell, Character target, int amount, double ratioPct) {
		var copyIncreasePct = getCopiedAmountIncreasePct(caster, spell);
		var targetStats = getAccumulatedTargetStats(spell, target, SPELL_DAMAGE, spell.getSchool());
		var damageTaken = targetStats.getAmountTaken();
		var damageTakenPct = targetStats.getAmountTakenPct();

		return multiplyByRatio(amount + damageTaken, ratioPct + copyIncreasePct, damageTakenPct);
	}

	public static double getCopiedAmountAsHeal(Character caster, Spell spell, Character target, int amount, double ratioPct) {
		var copyIncreasePct = getCopiedAmountIncreasePct(caster, spell);
		var targetStats = getAccumulatedTargetStats(spell, target, HEALING, spell.getSchool());
		var healingTaken = targetStats.getAmountTaken();
		var healingTakenPct = targetStats.getAmountTakenPct();

		return multiplyByRatio(amount + healingTaken, ratioPct + copyIncreasePct, healingTakenPct);
	}

	public static double getCopiedAmountAsManaGain(Character caster, Spell spell, Character target, int amount, double ratioPct) {
		var copyIncreasePct = getCopiedAmountIncreasePct(caster, spell);

		return multiplyByRatio(amount, ratioPct + copyIncreasePct, 0);
	}

	private static double getCopiedAmountIncreasePct(Character caster, Spell spell) {
		if (!(spell instanceof Ability ability)) {
			return 0;
		}

		var args = AttributeConditionArgs.forSpell(caster, ability, null);
		var accumulatedCopyPct = new AccumulatedSingleStat(COPY_PCT, args);

		accumulateEffects(caster, accumulatedCopyPct);

		return accumulatedCopyPct.totalValue;
	}
}
