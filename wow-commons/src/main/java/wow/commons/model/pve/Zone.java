package wow.commons.model.pve;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public class Zone {
	private final int id;
	private final String name;
	private final String shortName;
	private final GameVersion version;
	private final ZoneType zoneType;
	private final int partySize;
	private List<Boss> bosses;

	public Zone(int id, String name, String shortName, GameVersion version, ZoneType zoneType, int partySize) {
		this.id = id;
		this.name = name;
		this.shortName = shortName;
		this.version = version;
		this.zoneType = zoneType;
		this.partySize = partySize;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName != null ? shortName : name;
	}

	public GameVersion getVersion() {
		return version;
	}

	public ZoneType getZoneType() {
		return zoneType;
	}

	public int getPartySize() {
		return partySize;
	}

	public List<Boss> getBosses() {
		return bosses;
	}

	public void setBosses(List<Boss> bosses) {
		this.bosses = bosses;
	}

	public boolean isRaid() {
		return zoneType == ZoneType.Raid;
	}

	public boolean isDungeon() {
		return zoneType == ZoneType.Dungeon;
	}

	public boolean isInstance() {
		return isRaid() || isDungeon();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Zone)) return false;
		Zone zone = (Zone) o;
		return id == zone.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return getShortName();
	}
}
