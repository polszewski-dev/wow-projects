package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.character.CreatureType;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@AllArgsConstructor
public class EnemyPO implements Serializable {
	private CreatureType enemyType;
	private int levelDifference;
}
