package wow.scraper.model;

import wow.commons.model.pve.ZoneType;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public enum WowheadZoneType {
	Normal(0, ZoneType.Normal),
	Dungeon(2, ZoneType.Dungeon),
	Raid(3, ZoneType.Raid),
	Battleground(4, ZoneType.Battleground),
	Arena(6, ZoneType.Arena),
	;

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
