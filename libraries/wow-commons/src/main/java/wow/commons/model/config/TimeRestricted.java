package wow.commons.model.config;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public interface TimeRestricted {
	TimeRestriction getTimeRestriction();

	default PhaseId getEarliestPhaseId() {
		return getTimeRestriction().earliestPhaseId();
	}

	default GameVersionId getGameVersionId() {
		return getTimeRestriction().getGameVersionId();
	}

	default boolean isAvailableDuring(PhaseId phaseId) {
		var timeRestriction = getTimeRestriction();
		return timeRestriction == null || timeRestriction.isMetBy(phaseId);
	}
}
