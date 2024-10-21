package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.*;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@Setter
public class ItemImpl extends AbstractItemImpl implements Item {
	private List<Effect> effects;
	private ActivatedAbility activatedAbility;
	private ItemSet itemSet;
	private ItemSocketSpecification socketSpecification;
	private final WeaponStats weaponStats;
	private final Set<PveRole> pveRoles;

	public ItemImpl(
			int id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BasicItemInfo basicItemInfo,
			WeaponStats weaponStats,
			Set<PveRole> pveRoles
	) {
		super(id, description, timeRestriction, characterRestriction, basicItemInfo);
		this.weaponStats = weaponStats;
		this.pveRoles = pveRoles;
	}
}
