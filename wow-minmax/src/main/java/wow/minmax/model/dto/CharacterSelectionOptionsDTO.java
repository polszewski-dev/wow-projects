package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSelectionOptionsDTO {
	private List<PhaseDTO> phases;
	private List<EnemyTypeDTO> enemyTypes;
	private List<LevelDifferenceDTO> enemyLevelDiffs;
	private String lastModifiedCharacterId;
}
