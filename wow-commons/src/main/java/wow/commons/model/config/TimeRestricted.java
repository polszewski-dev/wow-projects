package wow.commons.model.config;

import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public interface TimeRestricted {
	TimeRestriction getTimeRestriction();

	default boolean isAvailableDuring(Phase phase) {
		return getTimeRestriction().isMetBy(phase);
	}
}
