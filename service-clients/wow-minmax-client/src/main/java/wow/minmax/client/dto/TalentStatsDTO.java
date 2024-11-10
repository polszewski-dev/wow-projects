package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.client.dto.TalentDTO;

/**
 * User: POlszewski
 * Date: 2024-03-28
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TalentStatsDTO {
	private TalentDTO talent;
	private String statEquivalent;
	private double spEquivalent;
}
