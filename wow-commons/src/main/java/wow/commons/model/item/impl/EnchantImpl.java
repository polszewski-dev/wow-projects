package wow.commons.model.item.impl;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;
import wow.commons.model.item.Enchant;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class EnchantImpl extends ConfigurationElementWithAttributesImpl<Integer> implements Enchant {
	private final List<ItemType> itemTypes;
	private final ItemRarity rarity;

	public EnchantImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Attributes attributes,
			List<ItemType> itemTypes,
			ItemRarity rarity
	) {
		super(id, description, timeRestriction, characterRestriction, attributes);
		this.itemTypes = itemTypes;
		this.rarity = rarity;
	}
}
