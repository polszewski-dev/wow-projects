package wow.commons.model.item.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.*;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ItemImpl extends AbstractItemImpl implements Item {
	private ItemSet itemSet;
	private final ItemSocketSpecification socketSpecification;
	private final WeaponStats weaponStats;
	private final Set<PveRole> pveRoles;

	public ItemImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BasicItemInfo basicItemInfo,
			ItemSocketSpecification socketSpecification,
			WeaponStats weaponStats,
			Set<PveRole> pveRoles
	) {
		super(id, description, timeRestriction, characterRestriction, basicItemInfo);
		this.socketSpecification = socketSpecification;
		this.weaponStats = weaponStats;
		this.pveRoles = pveRoles;
	}

	public void setItemSet(ItemSet itemSet) {
		this.itemSet = itemSet;
	}
}
