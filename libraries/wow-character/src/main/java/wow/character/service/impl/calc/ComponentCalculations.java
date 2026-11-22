package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedSpellStats;
import wow.character.model.snapshot.AccumulatedTargetStats;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.Spell;

import static java.lang.Math.clamp;
import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;
import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class ComponentCalculations extends Calculations {
	static AccumulatedTargetStats getAccumulatedTargetStats(Spell spell, Character target, PowerType powerType) {
		var targetStats = AccumulatedTargetStats.forSpellTarget(target, spell, powerType);

		accumulateEffects(target, targetStats);
		return targetStats;
	}

	static AccumulatedTargetStats getAccumulatedTargetStats(Spell spell, Character target, PowerType powerType, DirectCommand command) {
		var targetStats = AccumulatedTargetStats.forSpellTarget(target, spell, powerType, command);

		accumulateEffects(target, targetStats);
		return targetStats;
	}

	static AccumulatedTargetStats getAccumulatedTargetStats(Spell spell, Character target, PowerType powerType, PeriodicCommand command) {
		var targetStats = AccumulatedTargetStats.forSpellTarget(target, spell, powerType, command);

		accumulateEffects(target, targetStats);
		return targetStats;
	}

	static double getSpellCritPct(Character caster, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var baseStatInfo = caster.getBaseStatInfo();
		var cr = caster.getCombatRatingInfo();

		var intellect = (int) spellStats.getTotalIntellect();
		var intCrit = intellect / baseStatInfo.getIntellectPerCritPct();
		var ratingCrit = spellStats.getCritRating() / cr.getSpellCrit();
		var pctCrit = spellStats.getCritPct();
		var targetCrit = targetStats.getCritTakenPct();

		var totalCrit = intCrit + ratingCrit + pctCrit + targetCrit;

		return clamp(totalCrit, 0, 100);
	}

	static double getSpellCritCoeff(AccumulatedSpellStats spellStats) {
		var increasedCriticalEffect = spellStats.getCritEffectPct() / 100;
		var talentIncrease = spellStats.getCritEffectMultiplierPct() / 100;
		var extraCritCoeff = spellStats.getCritCoeffPct();

		return 1 + (0.5 + 1.5 * increasedCriticalEffect) * (1 + talentIncrease) + extraCritCoeff;
	}

	static int getSpellAmount(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return (int) (spellStats.getAmount() + targetStats.getAmountTaken());
	}

	static double getSpellAmountPct(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return spellStats.getAmountPct() + spellStats.getEffectPct() + targetStats.getAmountTakenPct();
	}

	static int getSpellPower(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return (int) (spellStats.getPower() + targetStats.getPowerTaken());
	}

	static double getPowerCoefficient(Coefficient coefficient, AccumulatedSpellStats spellStats) {
		var coeff = coefficient.value().value();
		var coeffBonus = spellStats.getPowerCoeffPct();

		return coeff + coeffBonus;
	}
}
