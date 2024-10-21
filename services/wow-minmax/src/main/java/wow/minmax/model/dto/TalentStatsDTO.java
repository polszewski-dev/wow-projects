package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
