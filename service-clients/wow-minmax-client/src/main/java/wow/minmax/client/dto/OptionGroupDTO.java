package wow.minmax.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-19
 */
public record OptionGroupDTO<T>(
		String groupId,
		List<OptionStatusDTO<T>> statuses
) {
}
