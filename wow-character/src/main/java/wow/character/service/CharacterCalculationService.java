package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.commons.model.attribute.primitive.PowerType;
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

	AccumulatedCastStats newAccumulatedCastStats(Character character, Ability ability);

	AccumulatedCostStats newAccumulatedCostStats(Character character, Ability ability);

	AccumulatedTargetStats newAccumulatedTargetStats(Character target, Spell spell, PowerType powerType, SpellSchool school);

	AccumulatedHitStats newAccumulatedHitStats(Character character, Spell spell, Character target);

	AccumulatedDurationStats newAccumulatedDurationStats(Character character, Spell spell, Character target);

	AccumulatedSpellStats newAccumulatedDirectComponentStats(Character character, Spell spell, Character target, DirectComponent directComponent);

	AccumulatedSpellStats newAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PeriodicComponent periodicComponent);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character);

	BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedBaseStats stats);

	SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability);

	SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, AccumulatedCastStats castStats);

	SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, BaseStatsSnapshot baseStats);

	SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, AccumulatedCostStats costStats);

	double getSpellHitPct(Character character, Spell spell, Character target);

	double getSpellHitPct(Character character, Spell spell, Character target, AccumulatedHitStats hitStats);

	EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target);

	EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target, AccumulatedDurationStats durationStats, AccumulatedTargetStats targetStats);

	DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats);

	DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats);

	PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, BaseStatsSnapshot baseStats);

	PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats);

	StatSummary getStatSummary(Character character);
}
