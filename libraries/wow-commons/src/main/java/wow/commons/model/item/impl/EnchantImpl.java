package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Enchant;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@Setter
public class EnchantImpl implements Enchant {
	private final int id;
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final List<ItemType> itemTypes;
	private final List<ItemSubType> itemSubTypes;
	private final int requiredItemLevel;
	private final ItemRarity rarity;
	private final Set<PveRole> pveRoles;
	private final int appliedEnchantId;
	private Effect effect;

	public EnchantImpl(
			int id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			List<ItemType> itemTypes,
			List<ItemSubType> itemSubTypes,
			int requiredItemLevel,
			ItemRarity rarity,
			Set<PveRole> pveRoles,
			int appliedEnchantId
	) {
		this.id = id;
		this.description = description;
		this.timeRestriction = timeRestriction;
		this.characterRestriction = characterRestriction;
		this.itemTypes = itemTypes;
		this.itemSubTypes = itemSubTypes;
		this.requiredItemLevel = requiredItemLevel;
		this.rarity = rarity;
		this.pveRoles = pveRoles;
		this.appliedEnchantId = appliedEnchantId;
	}

	@Override
	public String toString() {
		return getName();
	}
}
