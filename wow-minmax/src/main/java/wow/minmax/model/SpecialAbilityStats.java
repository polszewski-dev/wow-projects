package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Data
@AllArgsConstructor
public class SpecialAbilityStats {
	private SpecialAbility ability;
	private double spEquivalent;
}
