package wow.estimator.service;

import wow.character.model.build.Rotation;
import wow.character.model.snapshot.StatSummary;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.TalentId;
import wow.estimator.model.*;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface CalculationService {
	double getSpEquivalent(TalentId talentId, Player player);

	double getRotationDps(Player player, Rotation rotation, EffectList effectList, EffectList targetEffectList);

	double getRotationDps(Player player, Rotation rotation, AccumulatedRotationStats rotationStats);

	AccumulatedRotationStats getAccumulatedRotationStats(Player player, Rotation rotation);

	AccumulatedRotationStats getAccumulatedRotationStats(Player player, Rotation rotation, EffectList effectList, EffectList targetEffectList);

	RotationStats getRotationStats(Player player, Rotation rotation);

	SpellStats getSpellStats(Player player, Ability ability, boolean usesCombatRatings, double equivalentAmount);

	StatSummary getCurrentStats(Player player);

	StatSummary getStats(Player player, BuffCategory... buffCategories);

	StatSummary getEquipmentStats(Player player);

	SpecialAbilityStats getSpecialAbilityStats(SpecialAbility specialAbility, Player player);
}
