package wow.commons.model.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.sources.Source;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
@AllArgsConstructor
@Getter
public class BasicItemInfo {
	@NonNull
	private final ItemType itemType;
	private final ItemSubType itemSubType;
	@NonNull
	private final ItemRarity rarity;
	@NonNull
	private final Binding binding;
	private final boolean unique;
	private final int itemLevel;
	@NonNull
	private final Set<Source> sources;
}
