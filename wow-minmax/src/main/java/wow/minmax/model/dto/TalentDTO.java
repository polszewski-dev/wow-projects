package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
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
