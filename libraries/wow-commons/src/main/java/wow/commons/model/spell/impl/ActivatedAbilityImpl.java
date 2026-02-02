package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.CooldownGroup;
import wow.commons.model.spell.GroupCooldownId;

import static wow.commons.model.effect.EffectSource.ItemSource;

/**
 * User: POlszewski
 * Date: 2023-10-24
 */
@Getter
@RequiredArgsConstructor
public class ActivatedAbilityImpl extends AbilityImpl implements ActivatedAbility {
	private final CooldownGroup cooldownGroup;
	private EffectSource source;

	public void attachSource(EffectSource source) {
		this.source = source;
	}

	@Override
	public GroupCooldownId getGroupCooldownId() {
		var cooldownId = GroupCooldownId.of(cooldownGroup);

		if (cooldownId != null) {
			return cooldownId;
		}

		if (getSourceItemType() == ItemType.TRINKET) {
			return GroupCooldownId.TRINKET;
		}

		return null;
	}

	private ItemType getSourceItemType() {
		if (source instanceof ItemSource(var item)) {
			return item.getItemType();
		}
		return null;
	}
}
