package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.character.service.CharacterCalculationService;
import wow.character.service.impl.calc.*;
import wow.commons.model.AnyDuration;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;

import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
@Service
@AllArgsConstructor
public class CharacterCalculationServiceImpl implements CharacterCalculationService {
	@Override
	public BaseStatsSnapshot getBaseStatsSnapshot(Character character) {
		return BaseStatsCalculations.getBaseStatsSnapshot(character);
	}

	@Override
	public BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedOwnerStats stats) {
		return BaseStatsCalculations.getBaseStatsSnapshot(character, stats);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Character caster, Ability ability, Character target) {
		return CastCalculations.getSpellCastSnapshot(caster, ability, target);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Character caster, Ability ability, AccumulatedCasterStats castStats) {
		return CastCalculations.getSpellCastSnapshot(caster, ability, castStats);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Character caster, Ability ability, Character target) {
		return CostCalculations.getSpellCostSnapshot(caster, ability, target);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Character caster, Ability ability, AccumulatedCasterStats costStats) {
		return CostCalculations.getSpellCostSnapshot(caster, ability, costStats);
	}

	@Override
	public double getSpellHitPct(Character caster, Spell spell, Character target) {
		return HitCalculations.getSpellHitPct(caster, spell, target);
	}

	@Override
	public double getSpellHitPct(Character caster, Character target, AccumulatedCasterStats hitStats) {
		return HitCalculations.getSpellHitPct(caster, target, hitStats);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Character caster, Spell spell, Character target, ApplyEffect command) {
		return EffectCalculations.getEffectDurationSnapshot(caster, spell, target, command);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Character caster, Spell spell, ApplyEffect command, AccumulatedCasterStats durationStats, AccumulatedTargetStats receivedEffectStats) {
		return EffectCalculations.getEffectDurationSnapshot(caster, spell, command, durationStats, receivedEffectStats);
	}

	@Override
	public AnyDuration getSummonDuration(Character caster, Spell spell, AnyDuration baseDuration) {
		return SummonCalculations.getSummonDuration(caster, spell, baseDuration);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Character caster, Spell spell, Character target, DealDamageDirectly command) {
		return DirectComponentCalculations.getDirectSpellDamageSnapshot(caster, spell, target, command);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectHealingSnapshot(Character caster, Spell spell, Character target, HealDirectly command) {
		return DirectComponentCalculations.getDirectHealingSnapshot(caster, spell, target, command);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character caster, ChangeHealthDirectly command, AccumulatedCasterStats spellStats, AccumulatedTargetStats targetStats) {
		return DirectComponentCalculations.getDirectSpellComponentSnapshot(caster, command, spellStats, targetStats);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Character caster, Spell spell, Character target, DealDamagePeriodically command) {
		return PeriodicComponentCalculations.getPeriodicSpellDamageSnapshot(caster, spell, target, command);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Character caster, Spell spell, Character target, HealPeriodically command) {
		return PeriodicComponentCalculations.getPeriodicHealingSnapshot(caster, spell, target, command);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(ChangeHealthPeriodically command, AccumulatedCasterStats spellStats, AccumulatedTargetStats targetStats) {
		return PeriodicComponentCalculations.getPeriodicComponentSnapshot(command, spellStats, targetStats);
	}

	@Override
	public RegenSnapshot getRegenSnapshot(Character character) {
		return RegenCalculations.getRegenSnapshot(character);
	}

	@Override
	public StatSummary getStatSummary(Character character) {
		return StatsSummaryCalculations.getStatSummary(character);
	}

	@Override
	public EffectAugmentations getEffectAugmentations(Character caster, Spell spell, Character target) {
		return EffectCalculations.getEffectAugmentations(caster, spell, target);
	}

	@Override
	public double getCopiedAmountAsDamage(Character caster, Spell spell, Character target, int amount, double ratioPct) {
		return CopyCalculations.getCopiedAmountAsDamage(caster, spell, target, amount, ratioPct);
	}

	@Override
	public double getCopiedAmountAsHeal(Character caster, Spell spell, Character target, int amount, double ratioPct) {
		return CopyCalculations.getCopiedAmountAsHeal(caster, spell, target, amount, ratioPct);
	}

	@Override
	public double getCopiedAmountAsManaGain(Character caster, Spell spell, int amount, double ratioPct) {
		return CopyCalculations.getCopiedAmountAsManaGain(caster, spell, amount, ratioPct);
	}
}
