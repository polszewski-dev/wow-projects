package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
public enum GameVersionId {
	VANILLA(60, 300),
	TBC(70, 375),
	WOTLK(80, 450);

	private final int maxLevel;
	private final int maxProfession;

	public static GameVersionId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public PhaseId getEarliestPhase() {
		return Stream.of(PhaseId.values()).filter(x -> x.getGameVersionId() == this).findFirst().orElseThrow();
	}
}
