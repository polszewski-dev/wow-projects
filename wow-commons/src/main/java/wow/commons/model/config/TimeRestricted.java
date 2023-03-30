package wow.commons.model.config;

import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public interface TimeRestricted {
	TimeRestriction getTimeRestriction();

	default boolean isAvailableDuring(PhaseId phaseId) {
		return getTimeRestriction().isMetBy(phaseId);
	}
}
