package wow.commons.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeTarget;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.component.*;
import wow.commons.model.effect.impl.EmptyEffect;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;

import java.util.List;

import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2023-08-31
 */
public interface Effect extends Described, TimeRestricted {
	Effect EMPTY = new EmptyEffect();

	EffectId getId();

	EffectCategory getCategory();

	EffectSource getSource();

	List<AbilityId> getAugmentedAbilities();

	int getMaxStacks();

	EffectScope getScope();

	EffectExclusionGroup getExclusionGroup();

	PeriodicComponent getPeriodicComponent();

	default List<PeriodicCommand> getPeriodicCommands() {
		var periodicComponent = getPeriodicComponent();

		return periodicComponent != null ? periodicComponent.commands() : List.of();
	}

	default Duration getTickInterval() {
		return getPeriodicComponent() != null ? getPeriodicComponent().tickInterval() : null;
	}

	ModifierComponent getModifierComponent();

	List<Attribute> getModifierAttributeList();

	AbsorptionComponent getAbsorptionComponent();

	List<SpellSchool> getPreventedSchools();

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
		return hasPeriodicComponent() && getPeriodicComponent().commands().stream().anyMatch(x -> x.type() == componentType);
	}

	default boolean hasAbsorptionComponent() {
		return getAbsorptionComponent() != null;
	}

	default boolean isSchoolPrevented(SpellSchool school) {
		return school != null && getPreventedSchools().contains(school);
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
