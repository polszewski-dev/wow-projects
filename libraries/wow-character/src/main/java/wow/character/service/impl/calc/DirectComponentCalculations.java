package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedSpellStats;
import wow.character.model.snapshot.AccumulatedTargetStats;
import wow.character.model.snapshot.DirectSpellComponentSnapshot;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.ComponentCommand;

import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class DirectComponentCalculations extends ComponentCalculations {
	public static AccumulatedSpellStats newAccumulatedDirectComponentStats(Character caster, Spell spell, Character target, PowerType powerType, ComponentCommand.ChangeHealthDirectly command) {
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, target, powerType, command.school());

		conditionArgs.setDirect(true);

		return newAccumulatedSpellStats(conditionArgs);
	}

	private static AccumulatedSpellStats getAccumulatedDirectComponentStats(Character caster, Spell spell, Character target, PowerType powerType, ComponentCommand.ChangeHealthDirectly command) {
		var spellStats = newAccumulatedDirectComponentStats(caster, spell, target, powerType, command);

		accumulateEffects(caster, spellStats);
		return spellStats;
	}

	public static DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Character caster, Spell spell, Character target, ComponentCommand.DealDamageDirectly command) {
		return getDirectSpellComponentSnapshot(caster, spell, target, command, SPELL_DAMAGE);
	}

	public static DirectSpellComponentSnapshot getDirectHealingSnapshot(Character caster, Spell spell, Character target, ComponentCommand.HealDirectly command) {
		return getDirectSpellComponentSnapshot(caster, spell, target, command, HEALING);
	}

	private static DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character caster, Spell spell, Character target, ComponentCommand.ChangeHealthDirectly command, PowerType powerType) {
		var spellStats = getAccumulatedDirectComponentStats(caster, spell, target, powerType, command);
		var targetStats = getAccumulatedTargetStats(spell, target, powerType, command.school());

		return getDirectSpellComponentSnapshot(caster, spell, target, command, spellStats, targetStats);
	}

	public static DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character caster, Spell spell, Character target, ComponentCommand.ChangeHealthDirectly command, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var snapshot = new DirectSpellComponentSnapshot(command);

		var critPct = getSpellCritPct(caster, spellStats, targetStats);
		var critCoeff = getSpellCritCoeff(spellStats);
		var amount = getSpellAmount(spellStats, targetStats);
		var amountPct = getSpellAmountPct(spellStats, targetStats);
		var power = getSpellPower(spellStats, targetStats);
		var powerPct = spellStats.getPowerPct();
		var coeff = getPowerCoefficient(command.coefficient(), spellStats);

		snapshot.setCritPct(critPct);
		snapshot.setCritCoeff(critCoeff);
		snapshot.setAmount(amount);
		snapshot.setAmountPct(amountPct);
		snapshot.setPower(power);
		snapshot.setPowerPct(powerPct);
		snapshot.setCoeff(coeff);

		return snapshot;
	}
}
