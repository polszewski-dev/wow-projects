package wow.scraper.model;

import wow.commons.model.pve.GameVersion;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public enum WowheadGameVersion {
	VANILLA(0, GameVersion.VANILLA),
	TBC(1, GameVersion.TBC),
	WOTLK(2, GameVersion.WOTLK);

	private final int code;
	private final GameVersion gameVersion;

	WowheadGameVersion(int code, GameVersion gameVersion) {
		this.code = code;
		this.gameVersion = gameVersion;
	}

	public static WowheadGameVersion fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}

	public int getCode() {
		return code;
	}

	public GameVersion getGameVersion() {
		return gameVersion;
	}
}