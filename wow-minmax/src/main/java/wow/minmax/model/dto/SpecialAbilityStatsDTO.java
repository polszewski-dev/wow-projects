package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
@Data
@AllArgsConstructor
public class SpecialAbilityStatsDTO {
	private String description;
	private String attributes;
	private String statEquivalent;
	private double spEquivalent;
	private String sourceName;
	private String sourceIcon;
}
