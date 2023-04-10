package wow.commons.model.item.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.attributes.complex.special.sources.EnchantSource;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;
import wow.commons.model.item.Enchant;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class EnchantImpl extends ConfigurationElementWithAttributesImpl<Integer> implements Enchant {
	private final List<ItemType> itemTypes;
	private final ItemRarity rarity;
	private final Set<PveRole> pveRoles;

	public EnchantImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			List<ItemType> itemTypes,
			ItemRarity rarity,
			Set<PveRole> pveRoles
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.itemTypes = itemTypes;
		this.rarity = rarity;
		this.pveRoles = pveRoles;
	}

	@Override
	protected SpecialAbilitySource getSpecialAbilitySource() {
		return new EnchantSource(this);
	}
}
