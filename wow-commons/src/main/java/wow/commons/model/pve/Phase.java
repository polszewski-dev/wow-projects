package wow.commons.model.pve;

import static wow.commons.model.pve.GameVersion.TBC;
import static wow.commons.model.pve.GameVersion.Vanilla;

/**
 * User: POlszewski
 * Date: 2022-10-24
 */
public enum Phase {
	Vanilla_P1(Vanilla),//mc
	Vanilla_P2(Vanilla),//dm
	Vanilla_P3(Vanilla),//bwl
	Vanilla_P4(Vanilla),//zg
	Vanilla_P5(Vanilla),//aq
	Vanilla_P6(Vanilla),//naxx

	TBC_P0(TBC),//pre-patch
	TBC_P1(TBC),//kara,gruul,mag
	TBC_P2(TBC),//ssc,tk
	TBC_P3(TBC),//mh,bt
	TBC_P4(TBC),//za
	TBC_P5(TBC),//swp
	;

	private final GameVersion gameVersion;

	Phase(GameVersion gameVersion) {
		this.gameVersion = gameVersion;
	}

	public static Phase parse(String value) {
		if (value == null) {
			return null;
		}
		return valueOf(value);
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
