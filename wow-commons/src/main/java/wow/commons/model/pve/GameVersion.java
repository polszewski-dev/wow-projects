package wow.commons.model.pve;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public enum GameVersion {
	VANILLA(60, 300),
	TBC(70, 375),
	WOTLK(80, 450);

	private final int maxLevel;
	private final int maxProfession;

	GameVersion(int maxLevel, int maxProfession) {
		this.maxLevel = maxLevel;
		this.maxProfession = maxProfession;
	}

	public static GameVersion parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getMaxProfession() {
		return maxProfession;
	}
}
