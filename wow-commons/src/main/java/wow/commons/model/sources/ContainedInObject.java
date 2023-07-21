package wow.commons.model.sources;

import wow.commons.model.pve.Zone;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-29
 */
public record ContainedInObject(
		int id,
		String name,
		List<Zone> zones
) implements Source {
	@Override
	public String toString() {
		return name;
	}
}
