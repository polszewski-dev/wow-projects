package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Data
@AllArgsConstructor
public class SpecialAbilityStats {
	private String description;
	private String ability;
	private Attributes statEquivalent;
}
