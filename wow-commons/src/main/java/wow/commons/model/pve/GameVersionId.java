package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
public enum GameVersionId {
	VANILLA(60),
	TBC(70),
	WOTLK(80);

	private final int maxLevel;

	public static GameVersionId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public static GameVersionId getLatestGameVersionId() {
		return Stream.of(values())
				.max(Comparator.comparing(Enum::ordinal))
				.orElseThrow();
	}

	public PhaseId getEarliestPhase() {
		return Stream.of(PhaseId.values()).filter(x -> x.getGameVersionId() == this).findFirst().orElseThrow();
	}
}
