package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalentDTO {
	private String name;
	private int rank;
	private int maxRank;
	private String icon;
	private String tooltip;
	private String statEquivalent;
	private double spEquivalent;
}
