package wow.commons.client.dto;

import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffIdAndRank;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record BuffDTO(
		BuffId buffId,
		int rank,
		String name,
		String attributes,
		String icon,
		String tooltip,
		boolean enabled
) {
	public BuffDTO withEnabled(boolean enabled) {
		return new BuffDTO(
			buffId, rank, name, attributes, icon, tooltip, enabled
		);
	}

	public BuffIdAndRank getId() {
		return new BuffIdAndRank(buffId, rank);
	}
}
