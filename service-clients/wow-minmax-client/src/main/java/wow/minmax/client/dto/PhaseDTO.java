package wow.minmax.client.dto;

import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
public record PhaseDTO(
		PhaseId id,
		String name,
		int maxLevel
) {
}
