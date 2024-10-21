package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.talent.TalentId;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@AllArgsConstructor
@Getter
@Setter
public class TalentPO implements Serializable {
	private TalentId talentId;
	private int rank;
	private String name;
}
