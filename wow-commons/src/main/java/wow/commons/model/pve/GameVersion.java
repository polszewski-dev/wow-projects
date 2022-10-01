package wow.commons.model.pve;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public enum GameVersion {
	Vanilla,
	TBC,
	WotLK
	;

	public static GameVersion parse(String line) {
		return valueOf(line);
	}
}
