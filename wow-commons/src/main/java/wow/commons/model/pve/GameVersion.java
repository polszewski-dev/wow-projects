package wow.commons.model.pve;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public enum GameVersion {
	Vanilla(60, 300),
	TBC(70, 375),
	WotLK(80, 450),
	;

	private final int maxLevel;
	private final int maxProfession;

	GameVersion(int maxLevel, int maxProfession) {
		this.maxLevel = maxLevel;
		this.maxProfession = maxProfession;
	}

	public static GameVersion parse(String line) {
		return valueOf(line);
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public int getMaxProfession() {
		return maxProfession;
	}
}
