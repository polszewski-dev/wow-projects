package wow.estimator.service;

import wow.commons.model.spell.AbilityId;
import wow.estimator.model.*;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public interface StatsService {
	List<AbilityStats> getAbilityStats(Player player, List<AbilityId> abilityIds, boolean usesCombatRatings, double equivalentAmount);

	CharacterStats getCharacterStats(Player player, boolean worldBuffsAllowed);

	List<SpecialAbilityStats> getSpecialAbilityStats(Player player);

	RotationStats getRotationStats(Player player, Rotation rotation);

	List<TalentStats> getTalentStats(Player player);
}
