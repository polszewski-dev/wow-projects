package wow.commons.model.config;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public record TimeRestriction(PhaseId earliestPhaseId) {
	public TimeRestriction {
		Objects.requireNonNull(earliestPhaseId);
	}

	public static TimeRestriction of(GameVersionId gameVersionId) {
		return new TimeRestriction(gameVersionId.getEarliestPhase());
	}

	public static TimeRestriction of(PhaseId earliestPhaseId) {
		return new TimeRestriction(earliestPhaseId);
	}

	public static TimeRestriction of(GameVersionId gameVersionId, PhaseId phaseId) {
		if (gameVersionId == null) {
			return of(phaseId);
		}
		if (phaseId == null) {
			return of(gameVersionId);
		}
		if (phaseId.getGameVersionId() == gameVersionId) {
			return of(phaseId);
		}
		throw new IllegalArgumentException();
	}

	public boolean isMetBy(PhaseId phaseId) {
		if (!earliestPhaseId.isTheSameVersion(phaseId)) {
			return false;
		}
		return earliestPhaseId.isEarlierOrTheSame(phaseId);
	}

	public GameVersionId getGameVersionId() {
		return earliestPhaseId.getGameVersionId();
	}
}
