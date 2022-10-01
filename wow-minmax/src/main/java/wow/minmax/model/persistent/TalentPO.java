package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.talents.TalentId;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class TalentPO implements Serializable {
	private TalentId talentId;
	private int rank;
	private String name;
}
