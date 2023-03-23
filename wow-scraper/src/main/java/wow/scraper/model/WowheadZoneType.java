package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.ZoneType;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@AllArgsConstructor
@Getter
public enum WowheadZoneType {
	NORMAL(0, ZoneType.NORMAL),
	DUNGEON(2, ZoneType.DUNGEON),
	RAID(3, ZoneType.RAID),
	BATTLEGROUND(4, ZoneType.BATTLEGROUND),
	ARENA(6, ZoneType.ARENA);

	private final int code;
	private final ZoneType type;

	public static WowheadZoneType fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
