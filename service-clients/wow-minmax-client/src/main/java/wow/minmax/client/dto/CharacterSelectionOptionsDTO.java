package wow.minmax.client.dto;

import wow.commons.client.dto.EnemyTypeDTO;
import wow.commons.client.dto.LevelDifferenceDTO;
import wow.commons.client.dto.PhaseDTO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
public record CharacterSelectionOptionsDTO(
		List<PhaseDTO> phases,
		List<EnemyTypeDTO> enemyTypes,
		List<LevelDifferenceDTO> enemyLevelDiffs
) {
}
