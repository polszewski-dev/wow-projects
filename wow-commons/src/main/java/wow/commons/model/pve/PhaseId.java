package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import static wow.commons.model.pve.GameVersionId.*;

/**
 * User: POlszewski
 * Date: 2022-10-24
 */
@AllArgsConstructor
@Getter
public enum PhaseId {
	VANILLA_P1(VANILLA),//mc
	VANILLA_P2(VANILLA),//dm
	VANILLA_P2_5(VANILLA),//
	VANILLA_P3(VANILLA),//bwl
	VANILLA_P4(VANILLA),//zg
	VANILLA_P5(VANILLA),//aq
	VANILLA_P6(VANILLA),//naxx

	TBC_P0(TBC, true),//pre-patch
	TBC_P1(TBC),//kara,gruul,mag
	TBC_P2(TBC),//ssc,tk
	TBC_P3(TBC),//mh,bt
	TBC_P4(TBC),//za
	TBC_P5(TBC),//swp

	WOTLK_P0(WOTLK, true),
	WOTLK_P1(WOTLK);

	PhaseId(GameVersionId gameVersionId) {
		this(gameVersionId, false);
	}

	private final GameVersionId gameVersionId;
	private final boolean prepatch;

	public static PhaseId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isEarlierOrTheSame(PhaseId phaseId) {
		return this.compareTo(phaseId) <= 0;
	}

	public boolean isTheSameVersion(PhaseId phaseId) {
		return this.gameVersionId == phaseId.getGameVersionId();
	}
}
