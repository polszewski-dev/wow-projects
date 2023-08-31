package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.special.SpecialAbility;

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
