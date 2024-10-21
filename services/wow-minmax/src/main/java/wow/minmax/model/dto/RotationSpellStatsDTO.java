package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@AllArgsConstructor
@Getter
public class RotationSpellStatsDTO {
	private final AbilityDTO spell;
	private final double numCasts;
	private final double damage;
}
