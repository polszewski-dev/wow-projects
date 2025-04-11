package wow.evaluator.client.dto.upgrade;

import wow.commons.model.categorization.ItemRarity;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-04-11
 */
public record ItemLevelFilterDTO(
		Map<ItemRarity, Integer> minItemLevelByRarity
) {
	public ItemLevelFilterDTO {
		Objects.requireNonNull(minItemLevelByRarity);
	}
}
