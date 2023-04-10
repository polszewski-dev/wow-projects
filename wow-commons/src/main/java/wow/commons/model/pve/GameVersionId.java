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
	VANILLA(60, 4),
	TBC(70, 5),
	WOTLK(80, 6);

	private final int maxLevel;
	private final int dataEnv;

	public static GameVersionId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public PhaseId getEarliestPhase() {
		return Stream.of(PhaseId.values()).filter(x -> x.getGameVersionId() == this).findFirst().orElseThrow();
	}
}
