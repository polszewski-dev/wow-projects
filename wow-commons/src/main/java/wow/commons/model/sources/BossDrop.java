package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Zone;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class BossDrop extends Source {
	private final Boss boss;
	private final Zone zone;

	@Override
	public boolean isBossDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "Boss: " + boss;
	}
}
