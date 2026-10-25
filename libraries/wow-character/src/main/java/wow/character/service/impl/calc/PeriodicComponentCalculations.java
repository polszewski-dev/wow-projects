package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedSpellStats;
import wow.character.model.snapshot.AccumulatedTargetStats;
import wow.character.model.snapshot.PeriodicSpellComponentSnapshot;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.Spell;

import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class PeriodicComponentCalculations extends ComponentCalculations {
	public static AccumulatedSpellStats newAccumulatedPeriodicComponentStats(Character caster, Spell spell, Character target, PowerType powerType, PeriodicCommand command) {
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, target, powerType, command.school());

		conditionArgs.setPeriodic(true);

		return newAccumulatedSpellStats(conditionArgs);
	}

	private static AccumulatedSpellStats getAccumulatedPeriodicComponentStats(Character caster, Spell spell, Character target, PowerType powerType, PeriodicCommand command) {
		var spellStats = newAccumulatedPeriodicComponentStats(caster, spell, target, powerType, command);

		accumulateEffects(caster, spellStats);
		return spellStats;
	}

	public static PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Character caster, Spell spell, Character target, DealDamagePeriodically command) {
		return getPeriodicComponentSnapshot(caster, spell, target, command, SPELL_DAMAGE);
	}

	public static PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Character caster, Spell spell, Character target, HealPeriodically command) {
		return getPeriodicComponentSnapshot(caster, spell, target, command, HEALING);
	}

	private static PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(Character caster, Spell spell, Character target, ChangeHealthPeriodically command, PowerType powerType) {
		var spellStats = getAccumulatedPeriodicComponentStats(caster, spell, target, powerType, command);
		var targetStats = getAccumulatedTargetStats(spell, target, powerType, command.school());

		return getPeriodicComponentSnapshot(command, spellStats, targetStats);
	}

	public static PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(ChangeHealthPeriodically command, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var snapshot = new PeriodicSpellComponentSnapshot(command);

		var amount = getSpellAmount(spellStats, targetStats);
		var amountPct = getSpellAmountPct(spellStats, targetStats);
		var power = (int) spellStats.getPower();
		var powerPct = spellStats.getPowerPct();
		var coeff = getPowerCoefficient(command.coefficient(), spellStats);

		snapshot.setAmount(amount);
		snapshot.setAmountPct(amountPct);
		snapshot.setPower(power);
		snapshot.setPowerPct(powerPct);
		snapshot.setCoeff(coeff);

		return snapshot;
	}

}
