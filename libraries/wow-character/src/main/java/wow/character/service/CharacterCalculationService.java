package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.component.DirectComponent;

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

	AccumulatedDurationStats newAccumulatedDurationStats(Character character, Spell spell, Character target);

	AccumulatedReceivedEffectStats newAccumulatedReceivedEffectStats(Character target, Spell spell);

	AccumulatedSpellStats newAccumulatedDirectComponentStats(Character character, Spell spell, Character target, PowerType powerType, DirectComponent directComponent);

	AccumulatedSpellStats newAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PowerType powerType, PeriodicComponent periodicComponent);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedBaseStats stats);

	SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, Character target);

	SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, AccumulatedCastStats castStats);

	SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, Character target, BaseStatsSnapshot baseStats);

	SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, AccumulatedCostStats costStats);

	double getSpellHitPct(Character character, Spell spell, Character target);

	double getSpellHitPct(Character character, Spell spell, Character target, AccumulatedHitStats hitStats);

	EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target);

	EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats);

	DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats);

	DirectSpellComponentSnapshot getDirectHealingSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats);

	DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats);

	PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, BaseStatsSnapshot baseStats);

	PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Character character, Spell spell, Character target, BaseStatsSnapshot baseStats);

	PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(Character character, Spell spell, Character target, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats);

	RegenSnapshot getRegenSnapshot(Character character);

	StatSummary getStatSummary(Character character);

	EffectAugmentations getEffectAugmentations(Character character, Spell spell, Character target);

	double getCopiedAmountAsHeal(Character character, Spell spell, Character target, int amount, double ratioPct);

	double getCopiedAmountAsManaGain(Character character, Spell spell, Character target, int amount, double ratioPct);
}
