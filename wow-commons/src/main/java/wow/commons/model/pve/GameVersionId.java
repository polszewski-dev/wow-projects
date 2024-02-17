package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.Comparator;
import java.util.List;
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
		return PhaseId.getPhaseIdStream()
				.filter(x -> x.getGameVersionId() == this);
	}

	public Optional<GameVersionId> getPreviousVersion() {
		return Stream.of(values())
				.takeWhile(x -> x.compareTo(this) < 0)
				.max(Comparator.naturalOrder());
	}

	public List<PhaseId> getPhasesStartingFrom(PhaseId earliestPhaseId) {
		return getPhaseIdStream()
				.filter(earliestPhaseId::isTheSameVersion)
				.filter(earliestPhaseId::isEarlierOrTheSame)
				.toList();
	}
}
