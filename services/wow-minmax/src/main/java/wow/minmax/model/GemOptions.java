package wow.minmax.model;

import wow.commons.model.item.Gem;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record GemOptions(
		SocketType socketType,
		List<Gem> gems
) {
}
