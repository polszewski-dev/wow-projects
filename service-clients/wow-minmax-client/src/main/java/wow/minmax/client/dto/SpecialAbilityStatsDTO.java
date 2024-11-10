package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpecialAbilityStatsDTO {
	private String description;
	private String attributes;
	private String statEquivalent;
	private double spEquivalent;
	private String sourceName;
	private String sourceIcon;
}
