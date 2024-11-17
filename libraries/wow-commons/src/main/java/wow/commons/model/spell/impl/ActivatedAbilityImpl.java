package wow.commons.model.spell.impl;

import lombok.Getter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.GroupCooldownId;

/**
 * User: POlszewski
 * Date: 2023-10-24
 */
@Getter
public class ActivatedAbilityImpl extends AbilityImpl implements ActivatedAbility {
	private EffectSource source;

	public void attachSource(EffectSource source) {
		this.source = source;
	}

	@Override
	public GroupCooldownId getGroupCooldownId() {
		return getSourceItemType() == ItemType.TRINKET ? GroupCooldownId.TRINKET : null;
	}

	private ItemType getSourceItemType() {
		if (source instanceof ItemSource(var item)) {
			return item.getItemType();
		}
		return null;
	}
}
