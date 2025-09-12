package wow.minmax.client.dto;

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
