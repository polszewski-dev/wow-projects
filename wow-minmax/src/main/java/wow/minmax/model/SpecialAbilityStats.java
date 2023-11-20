package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@AllArgsConstructor
@Getter
@Setter
public class SpecialAbilityStats {
	private SpecialAbility ability;
	private double spEquivalent;
}
