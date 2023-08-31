package wow.commons.model.config;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public record TimeRestriction(List<GameVersionId> versions, PhaseId phaseId) {
	public static final TimeRestriction EMPTY = new TimeRestriction(List.of(), null);

	public TimeRestriction {
		Objects.requireNonNull(versions);

		if (phaseId != null && versions.stream().anyMatch(version -> version != phaseId.getGameVersionId())) {
			throw new IllegalArgumentException("Phase and version filters must match");
		}
	}

	public static TimeRestriction of(GameVersionId gameVersionId) {
		return new TimeRestriction(List.of(gameVersionId), null);
	}

	public static TimeRestriction of(PhaseId phaseId) {
		return new TimeRestriction(List.of(), phaseId);
	}

	public static TimeRestriction of(GameVersionId gameVersionId, PhaseId phaseId) {
		if (gameVersionId == null) {
			return of(phaseId);
		}
		if (phaseId == null || phaseId == gameVersionId.getEarliestPhase()) {
			return of(gameVersionId);
		}
		return new TimeRestriction(List.of(gameVersionId), phaseId);
	}

	public boolean isMetBy(PhaseId phaseId) {
		if (!this.versions.isEmpty() && !this.versions.contains(phaseId.getGameVersionId())) {
			return false;
		}
		return this.phaseId == null || this.phaseId.isEarlierOrTheSame(phaseId) && this.phaseId.isTheSameVersion(phaseId);
	}

	public GameVersionId getUniqueVersion() {
		if (versions.isEmpty()) {
			return phaseId.getGameVersionId();
		}
		return getUniqueResult(versions).orElseThrow();
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (!versions.isEmpty()) {
			parts.add(String.format("versions: %s", versions));
		}
		if (phaseId != null) {
			parts.add(String.format("phase: %s", phaseId));
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
