package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.buffs.BuffIdAndRank;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class BuffPO implements Serializable {
	private BuffId buffId;
	private int rank;
	private String name;

	public BuffIdAndRank getId() {
		return new BuffIdAndRank(buffId, rank);
	}
}
