package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.commons.model.AnyDuration;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;

import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
public interface CharacterCalculationService {
	BaseStatsSnapshot getBaseStatsSnapshot(Character character);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedOwnerStats stats);

	SpellCastSnapshot getSpellCastSnapshot(Character caster, Ability ability, Character target);

	SpellCastSnapshot getSpellCastSnapshot(Character caster, Ability ability, AccumulatedCasterStats castStats);

	SpellCostSnapshot getSpellCostSnapshot(Character caster, Ability ability, Character target);

	SpellCostSnapshot getSpellCostSnapshot(Character caster, Ability ability, AccumulatedCasterStats costStats);

	double getSpellHitPct(Character caster, Spell spell, Character target);

	double getSpellHitPct(Character caster, Character target, AccumulatedCasterStats hitStats);

	EffectDurationSnapshot getEffectDurationSnapshot(Character caster, Spell spell, Character target, ApplyEffect command);

	EffectDurationSnapshot getEffectDurationSnapshot(Character caster, Spell spell, ApplyEffect command, AccumulatedCasterStats durationStats, AccumulatedTargetStats receivedEffectStats);

	AnyDuration getSummonDuration(Character caster, Spell spell, AnyDuration baseDuration);

	DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Character caster, Spell spell, Character target, DealDamageDirectly command);

	DirectSpellComponentSnapshot getDirectHealingSnapshot(Character caster, Spell spell, Character target, HealDirectly command);

	DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character caster, ChangeHealthDirectly command, AccumulatedCasterStats spellStats, AccumulatedTargetStats targetStats);

	PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Character caster, Spell spell, Character target, DealDamagePeriodically command);

	PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Character caster, Spell spell, Character target, HealPeriodically command);

	PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(ChangeHealthPeriodically command, AccumulatedCasterStats spellStats, AccumulatedTargetStats targetStats);

	RegenSnapshot getRegenSnapshot(Character character);

	StatSummary getStatSummary(Character character);

	EffectAugmentations getEffectAugmentations(Character caster, Spell spell, Character target);

	double getCopiedAmountAsDamage(Character caster, Spell spell, Character target, int amount, double ratioPct);

	double getCopiedAmountAsHeal(Character caster, Spell spell, Character target, int amount, double ratioPct);

	double getCopiedAmountAsManaGain(Character caster, Spell spell, int amount, double ratioPct);
}
