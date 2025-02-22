package wow.commons.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeTarget;
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

	List<AbilityId> getAugmentedAbilities();

	int getMaxStacks();

	EffectScope getScope();

	EffectExclusionGroup getExclusionGroup();

	PeriodicComponent getPeriodicComponent();

	default Duration getTickInterval() {
		return getPeriodicComponent() != null ? getPeriodicComponent().tickInterval() : null;
	}

	ModifierComponent getModifierComponent();

	List<Attribute> getModifierAttributeList();

	AbsorptionComponent getAbsorptionComponent();

	List<StatConversion> getStatConversions();

	List<Event> getEvents();

	default boolean hasModifierComponent() {
		return getModifierComponent() != null;
	}

	default boolean hasModifierComponentOnly() {
		return hasModifierComponent() && !hasAnyNonModifierComponents();
	}

	default boolean hasAnyNonModifierComponents() {
		return hasAugmentedAbilities() ||
				hasPeriodicComponent() ||
				hasAbsorptionComponent() ||
				hasStatConversions() ||
				hasEvents();
	}

	default boolean hasAugmentedAbilities() {
		return !getAugmentedAbilities().isEmpty();
	}

	default boolean augments(AbilityId abilityId) {
		return getAugmentedAbilities().contains(abilityId);
	}

	default boolean hasPeriodicComponent() {
		return getPeriodicComponent() != null;
	}

	default boolean hasPeriodicComponent(ComponentType componentType) {
		return hasPeriodicComponent() && getPeriodicComponent().type() == componentType;
	}

	default boolean hasAbsorptionComponent() {
		return getAbsorptionComponent() != null;
	}

	default boolean hasStatConversions() {
		return !getStatConversions().isEmpty();
	}

	default boolean hasEvents() {
		return !getEvents().isEmpty();
	}

	default boolean isAura() {
		return hasModifierComponent() && getModifierAttributeList().stream()
				.anyMatch(x -> x.id().getTarget() == AttributeTarget.PARTY);
	}
}
