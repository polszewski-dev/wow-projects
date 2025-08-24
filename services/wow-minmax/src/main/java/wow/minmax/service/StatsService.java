package wow.minmax.service;

import wow.estimator.client.dto.stats.*;
import wow.minmax.model.CharacterId;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public interface StatsService {
	GetSpellStatsResponseDTO getSpellStats(CharacterId characterId);

	GetCharacterStatsResponseDTO getCharacterStats(CharacterId characterId);

	GetSpecialAbilityStatsResponseDTO getSpecialAbilityStats(CharacterId characterId);

	GetRotationStatsResponseDTO getRotationStats(CharacterId characterId);

	GetTalentStatsResponseDTO getTalentStats(CharacterId characterId);
}
