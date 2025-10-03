package wow.minmax.client.dto;

import wow.commons.model.pve.FactionExclusionGroupId;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
public record ExclusiveFactionDTO(
		int id,
		String name,
		FactionExclusionGroupId exclusionGroupId
) {
}
