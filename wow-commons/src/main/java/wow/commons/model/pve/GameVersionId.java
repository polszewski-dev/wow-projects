package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
public enum GameVersionId {
	VANILLA,
	TBC,
	WOTLK;

	public static GameVersionId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public PhaseId getEarliestPhase() {
		return getPhaseIdStream()
				.min(Comparator.naturalOrder())
				.orElseThrow();
	}

	public PhaseId getEarliestNonPrepatchPhase() {
		return getPhaseIdStream()
				.filter(x -> !x.isPrepatch())
				.min(Comparator.naturalOrder())
				.orElseThrow();
	}

	public PhaseId getLastPhase() {
		return getPhaseIdStream()
				.max(Comparator.naturalOrder())
				.orElseThrow();
	}

	public Optional<PhaseId> getPrepatchPhase() {
		return getPhaseIdStream()
				.filter(PhaseId::isPrepatch)
				.findAny();
	}

	private Stream<PhaseId> getPhaseIdStream() {
		return Stream.of(PhaseId.values())
				.filter(x -> x.getGameVersionId() == this);
	}

	public Optional<GameVersionId> getPreviousVersion() {
		GameVersionId previous = null;
		for (GameVersionId value : values()) {
			if (value == this) {
				return Optional.ofNullable(previous);
			}
			previous = value;
		}
		return Optional.ofNullable(previous);
	}
}
