package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
public enum GameVersion {
	VANILLA(60, 300),
	TBC(70, 375),
	WOTLK(80, 450);

	private final int maxLevel;
	private final int maxProfession;

	public static GameVersion parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
