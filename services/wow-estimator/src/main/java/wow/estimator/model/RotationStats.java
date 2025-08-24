package wow.estimator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@AllArgsConstructor
@Getter
public class RotationStats {
	private final List<RotationSpellStats> statList;
	private final double dps;
	private final double totalDamage;
}
