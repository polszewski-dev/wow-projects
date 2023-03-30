package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.GameVersionId.VANILLA;

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

	TBC_P0(TBC),//pre-patch
	TBC_P1(TBC),//kara,gruul,mag
	TBC_P2(TBC),//ssc,tk
	TBC_P3(TBC),//mh,bt
	TBC_P4(TBC),//za
	TBC_P5(TBC);//swp

	private final GameVersionId gameVersionId;

	public static PhaseId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isEarlier(PhaseId phaseId) {
		return this.compareTo(phaseId) < 0;
	}

	public boolean isEarlierOrTheSame(PhaseId phaseId) {
		return this.compareTo(phaseId) <= 0;
	}

	public boolean isTheSameVersion(PhaseId phaseId) {
		return this.gameVersionId == phaseId.getGameVersionId();
	}
}
