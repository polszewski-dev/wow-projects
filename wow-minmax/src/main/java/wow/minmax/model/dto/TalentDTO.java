package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.talents.TalentId;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class TalentDTO {
	private TalentId talentId;
	private int rank;
	private String name;
}
