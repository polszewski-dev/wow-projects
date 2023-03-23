package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.GameVersion;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@AllArgsConstructor
@Getter
public enum WowheadGameVersion {
	VANILLA(0, GameVersion.VANILLA),
	TBC(1, GameVersion.TBC),
	WOTLK(2, GameVersion.WOTLK);

	private final int code;
	private final GameVersion gameVersion;

	public static WowheadGameVersion fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
