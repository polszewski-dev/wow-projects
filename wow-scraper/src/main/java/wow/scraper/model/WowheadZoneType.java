package wow.scraper.model;

import wow.commons.model.pve.ZoneType;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public enum WowheadZoneType {
	NORMAL(0, ZoneType.NORMAL),
	DUNGEON(2, ZoneType.DUNGEON),
	RAID(3, ZoneType.RAID),
	BATTLEGROUND(4, ZoneType.BATTLEGROUND),
	ARENA(6, ZoneType.ARENA);

	private final int code;
	private final ZoneType type;

	WowheadZoneType(int code, ZoneType type) {
		this.code = code;
		this.type = type;
	}

	public static WowheadZoneType fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}

	public int getCode() {
		return code;
	}

	public ZoneType getType() {
		return type;
	}
}
