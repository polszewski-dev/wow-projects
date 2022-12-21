package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.character.CreatureType;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@AllArgsConstructor
@Getter
public class EnemyInfo {
	private final CreatureType enemyType;
}
