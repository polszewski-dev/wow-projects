package wow.commons.model.effect.impl;

import lombok.Getter;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.*;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2026-04-08
 */
@Getter
public class AttributeEffect implements Effect {
	private final List<AbilityId> augmentedAbilities;
	private final ModifierComponent modifierComponent;
	private final List<Attribute> modifierAttributeList;
	private final EffectSource source;
	private final Description description;

	public AttributeEffect(List<AbilityId> augmentedAbilities, ModifierComponent modifierComponent, EffectSource source, Description description) {
		Objects.requireNonNull(augmentedAbilities);
		this.augmentedAbilities = augmentedAbilities;
		this.modifierComponent = modifierComponent;
		this.modifierAttributeList = (modifierComponent != null) ? modifierComponent.attributes().list() : null;
		this.source = source;
		this.description = description;
	}

	public AttributeEffect(Attributes attributes) {
		this(List.of(), attributes, null);
	}

	public AttributeEffect(Attributes attributes, String tooltip) {
		this(List.of(), attributes, tooltip);
	}

	public AttributeEffect(List<AbilityId> augmentedAbilities, Attributes attributes, String tooltip) {
		this(
				augmentedAbilities,
				new ModifierComponent(attributes),
				null,
				tooltip != null ? new Description("", null, tooltip) : null
		);
	}

	@Override
	public String toString() {
		if (description == null) {
			return "";
		}
		return getName().isEmpty() ? getTooltip() : getName();
	}

	@Override
	public EffectId getId() {
		return null;
	}

	@Override
	public EffectCategory getCategory() {
		return null;
	}

	@Override
	public int getMaxStacks() {
		return 1;
	}

	@Override
	public int getMaxCounters() {
		return 0;
	}

	@Override
	public EffectScope getScope() {
		return null;
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
	public AbsorptionComponent getAbsorptionComponent() {
		return null;
	}

	@Override
	public List<SpellSchool> getPreventedSchools() {
		return List.of();
	}

	@Override
	public List<StatConversion> getStatConversions() {
		return List.of();
	}

	@Override
	public List<Event> getEvents() {
		return List.of();
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return null;
	}
}
