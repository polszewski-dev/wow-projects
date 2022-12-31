package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
@Data
@AllArgsConstructor
public class SpecialAbilityDTO {
	private String description;
	private String ability;
	private String statEquivalent;
}
