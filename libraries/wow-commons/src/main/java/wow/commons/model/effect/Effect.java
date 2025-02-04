package wow.commons.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.component.*;
import wow.commons.model.effect.impl.EmptyEffect;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-31
 */
public interface Effect extends Described, TimeRestricted {
	Effect EMPTY = new EmptyEffect();

	int getEffectId();

	EffectCategory getCategory();

	EffectSource getSource();

	AbilityId getAugmentedAbility();

	int getMaxStacks();

	PeriodicComponent getPeriodicComponent();

	Duration getTickInterval();

	ModifierComponent getModifierComponent();

	List<Attribute> getModifierAttributeList();

	AbsorptionComponent getAbsorptionComponent();

	List<StatConversion> getStatConversions();

	EffectIncreasePerEffectOnTarget getEffectIncreasePerEffectOnTarget();

	List<Event> getEvents();

	default boolean hasModifierComponentOnly() {
		return getModifierComponent() != null && !hasAnyNonModifierComponents();
	}

	default boolean hasAnyNonModifierComponents() {
		return getAugmentedAbility() != null ||
				getPeriodicComponent() != null ||
				getAbsorptionComponent() != null ||
				getTickInterval() != null ||
				!getStatConversions().isEmpty() ||
				getEffectIncreasePerEffectOnTarget() != null ||
				!getEvents().isEmpty();
	}

	default boolean hasPeriodicComponent(ComponentType componentType) {
		return getPeriodicComponent() != null && getPeriodicComponent().type() == componentType;
	}
}
