package wow.commons.model.source;

import wow.commons.model.pve.Zone;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public record ZoneDrop(List<Zone> zones) implements Source {
	@Override
	public boolean isZoneDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "Trash: " + zoneShortNames();
	}
}
