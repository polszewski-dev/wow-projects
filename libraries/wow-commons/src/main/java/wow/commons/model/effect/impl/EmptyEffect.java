package wow.commons.model.effect.impl;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.*;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-24
 */
public class EmptyEffect implements Effect {
	@Override
	public Description getDescription() {
		return new Description("", null, null);
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return null;
	}

	@Override
	public int getEffectId() {
		return 0;
	}

	@Override
	public EffectCategory getCategory() {
		return null;
	}

	@Override
	public EffectSource getSource() {
		return null;
	}

	@Override
	public List<AbilityId> getAugmentedAbilities() {
		return List.of();
	}

	@Override
	public int getMaxStacks() {
		return 0;
	}

	@Override
	public EffectScope getScope() {
		return EffectScope.PERSONAL;
	}

	@Override
	public EffectExclusionGroup getExclusionGroup() {
		return null;
	}

	@Override
	public PeriodicComponent getPeriodicComponent() {
		return null;
	}

	@Override
	public ModifierComponent getModifierComponent() {
		return null;
	}

	@Override
	public List<Attribute> getModifierAttributeList() {
		return List.of();
	}

	@Override
	public AbsorptionComponent getAbsorptionComponent() {
		return null;
	}

	@Override
	public List<StatConversion> getStatConversions() {
		return List.of();
	}

	@Override
	public List<Event> getEvents() {
		return List.of();
	}
}
