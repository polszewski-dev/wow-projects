package wow.commons.model.item;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.sources.Source;

import java.util.Objects;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public record BasicItemInfo(
		ItemType itemType,
		ItemSubType itemSubType,
		ItemRarity rarity,
		Binding binding,
		boolean unique,
		int itemLevel,
		Set<Source> sources
) {
	public BasicItemInfo {
		Objects.requireNonNull(itemType);
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(binding);
		Objects.requireNonNull(sources);
	}
}
