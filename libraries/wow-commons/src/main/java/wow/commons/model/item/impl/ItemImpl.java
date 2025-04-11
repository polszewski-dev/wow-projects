package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.*;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;
import java.util.Set;

import static wow.commons.model.categorization.PveRole.CASTER_DPS;
import static wow.commons.model.categorization.PveRole.HEALER;
import static wow.commons.model.categorization.WeaponSubType.WAND;

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

	@Override
	public boolean isSuitableFor(CharacterInfo character) {
		if (character.getLevel() < 40 && getItemSubType() == WAND && (character.getRole() == CASTER_DPS || character.getRole() == HEALER)) {
			return true;
		}
		return Item.super.isSuitableFor(character);
	}
}
