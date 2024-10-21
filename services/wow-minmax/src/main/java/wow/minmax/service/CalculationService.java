package wow.minmax.service;

import wow.character.model.build.Rotation;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.snapshot.StatSummary;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.TalentId;
import wow.minmax.model.*;
import wow.minmax.util.EffectList;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface CalculationService {
	double getSpEquivalent(TalentId talentId, PlayerCharacter character);

	double getRotationDps(PlayerCharacter character, Rotation rotation, EffectList effectList, EffectList targetEffectList);

	double getRotationDps(PlayerCharacter character, Rotation rotation, AccumulatedRotationStats rotationStats);

	AccumulatedRotationStats getAccumulatedRotationStats(PlayerCharacter character, Rotation rotation);

	AccumulatedRotationStats getAccumulatedRotationStats(PlayerCharacter character, Rotation rotation, EffectList effectList, EffectList targetEffectList);

	RotationStats getRotationStats(PlayerCharacter character, Rotation rotation);

	SpellStats getSpellStats(PlayerCharacter character, Ability ability);

	StatSummary getCurrentStats(PlayerCharacter character);

	StatSummary getStats(PlayerCharacter character, BuffCategory... buffCategories);

	StatSummary getEquipmentStats(PlayerCharacter character);

	SpecialAbilityStats getSpecialAbilityStats(SpecialAbility specialAbility, PlayerCharacter character);
}
