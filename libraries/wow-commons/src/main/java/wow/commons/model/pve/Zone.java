package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Zone implements TimeRestricted {
	private final int id;
	private final String name;
	private final String shortName;
	private final GameVersionId version;
	private final ZoneType zoneType;
	private final int partySize;
	private List<Npc> npcs;
	private final TimeRestriction timeRestriction;

	public String getShortName() {
		return shortName != null ? shortName : name;
	}

	public boolean isRaid() {
		return zoneType == ZoneType.RAID;
	}

	public boolean isDungeon() {
		return zoneType == ZoneType.DUNGEON;
	}

	public boolean isInstance() {
		return isRaid() || isDungeon();
	}

	@Override
	public String toString() {
		return getShortName();
	}
}
