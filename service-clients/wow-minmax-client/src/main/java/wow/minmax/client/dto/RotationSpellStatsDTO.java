package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.client.dto.AbilityDTO;

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
