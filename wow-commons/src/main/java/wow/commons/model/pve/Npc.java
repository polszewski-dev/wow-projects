package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-02-01
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Npc implements TimeRestricted {
	private final int id;
	private final String name;
	private final NpcType type;
	private final boolean boss;
	private final List<Zone> zones;
	private final TimeRestriction timeRestriction;

	public Optional<Zone> getZone(int zoneId) {
		return zones.stream()
				.filter(x -> x.getId() == zoneId)
				.findFirst();
	}

	@Override
	public String toString() {
		return name;
	}
}
