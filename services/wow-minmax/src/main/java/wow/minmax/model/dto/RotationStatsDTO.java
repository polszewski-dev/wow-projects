package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@AllArgsConstructor
@Getter
public class RotationStatsDTO {
	private final List<RotationSpellStatsDTO> statList;
	private final double dps;
	private final double totalDamage;
}
