package wow.minmax.service;

import wow.character.model.character.PlayerCharacter;
import wow.estimator.client.dto.stats.*;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public interface StatsService {
	GetAbilityStatsResponseDTO getAbilityStats(PlayerCharacter player);

	GetCharacterStatsResponseDTO getCharacterStats(PlayerCharacter player);

	GetSpecialAbilityStatsResponseDTO getSpecialAbilityStats(PlayerCharacter player);

	GetRotationStatsResponseDTO getRotationStats(PlayerCharacter player);

	GetTalentStatsResponseDTO getTalentStats(PlayerCharacter player);
}
