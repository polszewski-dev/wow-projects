package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.special.SpecialAbility;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Data
@AllArgsConstructor
public class SpecialAbilityStats {
	private SpecialAbility ability;
	private Attributes statEquivalent;
	private double spEquivalent;
}
