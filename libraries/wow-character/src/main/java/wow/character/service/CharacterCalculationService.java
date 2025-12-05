package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import static wow.commons.model.spell.component.ComponentCommand.*;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
public interface CharacterCalculationService {
	AccumulatedBaseStats newAccumulatedBaseStats(Character character);

	AccumulatedCastStats newAccumulatedCastStats(Character character, Ability ability, Character target);

	AccumulatedCostStats newAccumulatedCostStats(Character character, Ability ability, Character target);

	AccumulatedTargetStats newAccumulatedTargetStats(Character target, Spell spell, PowerType powerType, SpellSchool school);

	AccumulatedHitStats newAccumulatedHitStats(Character character, Spell spell, Character target);

	AccumulatedDurationStats newAccumulatedDurationStats(Character character, Spell spell, Character target, ApplyEffect command);

	AccumulatedReceivedEffectStats newAccumulatedReceivedEffectStats(Character target, Spell spell, ApplyEffect command);

	AccumulatedSpellStats newAccumulatedDirectComponentStats(Character character, Spell spell, Character target, PowerType powerType, ChangeHealthDirectly command);

	AccumulatedSpellStats newAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PowerType powerType, PeriodicCommand command);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedBaseStats stats);

	SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, Character target);

	SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, AccumulatedCastStats castStats);

	SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, Character target, BaseStatsSnapshot baseStats);

	SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, AccumulatedCostStats costStats);

	double getSpellHitPct(Character character, Spell spell, Character target);

	double getSpellHitPct(Character character, Spell spell, Character target, AccumulatedHitStats hitStats);

	EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target, ApplyEffect command);

	EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats);

	DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DealDamageDirectly command, BaseStatsSnapshot baseStats);

	DirectSpellComponentSnapshot getDirectHealingSnapshot(Character character, Spell spell, Character target, HealDirectly command, BaseStatsSnapshot baseStats);

	DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character character, Spell spell, Character target, ChangeHealthDirectly command, BaseStatsSnapshot baseStats, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats);

	PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, DealDamagePeriodically command, BaseStatsSnapshot baseStats);

	PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Character character, Spell spell, Character target, HealPeriodically command, BaseStatsSnapshot baseStats);

	PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(Character character, Spell spell, Character target, ChangeHealthPeriodically command, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats);

	RegenSnapshot getRegenSnapshot(Character character);

	StatSummary getStatSummary(Character character);

	EffectAugmentations getEffectAugmentations(Character character, Spell spell, Character target);

	double getCopiedAmountAsDamage(Character character, Spell spell, Character target, int amount, double ratioPct);

	double getCopiedAmountAsHeal(Character character, Spell spell, Character target, int amount, double ratioPct);

	double getCopiedAmountAsManaGain(Character character, Spell spell, Character target, int amount, double ratioPct);
}
