package wow.minmax.client.dto;

import wow.commons.client.dto.BuffDTO;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public record BuffStatusDTO(
		BuffDTO buff,
		boolean enabled
) {
}
