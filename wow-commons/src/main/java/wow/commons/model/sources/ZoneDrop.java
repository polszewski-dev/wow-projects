package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.pve.Zone;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class ZoneDrop extends Source {
	private final Zone zone;

	@Override
	public boolean isZoneDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "Trash: " + zone;
	}
}