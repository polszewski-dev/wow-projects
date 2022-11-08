package wow.commons.model.pve;

import wow.commons.util.EnumUtil;

import static wow.commons.model.pve.GameVersion.TBC;
import static wow.commons.model.pve.GameVersion.VANILLA;

/**
 * User: POlszewski
 * Date: 2022-10-24
 */
public enum Phase {
	VANILLA_P1(VANILLA),//mc
	VANILLA_P2(VANILLA),//dm
	VANILLA_P3(VANILLA),//bwl
	VANILLA_P4(VANILLA),//zg
	VANILLA_P5(VANILLA),//aq
	VANILLA_P6(VANILLA),//naxx

	TBC_P0(TBC),//pre-patch
	TBC_P1(TBC),//kara,gruul,mag
	TBC_P2(TBC),//ssc,tk
	TBC_P3(TBC),//mh,bt
	TBC_P4(TBC),//za
	TBC_P5(TBC);//swp

	private final GameVersion gameVersion;

	Phase(GameVersion gameVersion) {
		this.gameVersion = gameVersion;
	}

	public static Phase parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public GameVersion getGameVersion() {
		return gameVersion;
	}

	public boolean isEarlier(Phase phase) {
		return this.compareTo(phase) < 0;
	}

	public boolean isEarlierOrTheSame(Phase phase) {
		return this.compareTo(phase) <= 0;
	}
}
