package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffIdAndRank;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@AllArgsConstructor
@Getter
@Setter
public class BuffPO implements Serializable {
	private BuffId buffId;
	private int rank;
	private String name;

	public BuffIdAndRank getId() {
		return new BuffIdAndRank(buffId, rank);
	}
}
