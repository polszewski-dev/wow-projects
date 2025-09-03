package wow.minmax.model;

import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
public record CharacterSelectionOptions(
		List<Phase> phases,
		List<CreatureType> enemyTypes,
		List<Integer> enemyLevelDiffs
) {
}
