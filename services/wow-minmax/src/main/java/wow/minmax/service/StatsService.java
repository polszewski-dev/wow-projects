package wow.minmax.service;

import wow.estimator.client.dto.stats.*;
import wow.minmax.model.Player;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public interface StatsService {
	GetAbilityStatsResponseDTO getAbilityStats(Player player);

	GetCharacterStatsResponseDTO getCharacterStats(Player player);

	GetSpecialAbilityStatsResponseDTO getSpecialAbilityStats(Player player);

	GetRotationStatsResponseDTO getRotationStats(Player player);

	GetTalentStatsResponseDTO getTalentStats(Player player);
}
