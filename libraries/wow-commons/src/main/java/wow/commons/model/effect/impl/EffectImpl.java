package wow.commons.model.effect.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.attribute.Attribute;
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
 * Date: 2023-10-05
 */
@Getter
@Setter
public class EffectImpl implements Effect {
	private EffectId id;
	private EffectCategory category;
	private EffectSource source;
	private final List<AbilityId> augmentedAbilities;
	private Description description;
	private TimeRestriction timeRestriction;
	private int maxStacks;
	private int maxCounters;
	private EffectScope scope;
	private EffectExclusionGroup exclusionGroup;
	private PeriodicComponent periodicComponent;
	private ModifierComponent modifierComponent;
	private AbsorptionComponent absorptionComponent;
	private List<SpellSchool> preventedSchools;
	private List<StatConversion> statConversions;
	private List<Event> events;

	public EffectImpl(List<AbilityId> augmentedAbilities) {
		Objects.requireNonNull(augmentedAbilities);
		this.augmentedAbilities = augmentedAbilities;
	}

	@Override
	public List<Attribute> getModifierAttributeList() {
		if (modifierComponent == null) {
			return null;
		}
		return modifierComponent.attributes().list();
	}

	public void attachSource(EffectSource source) {
		this.source = source;
	}

	@Override
	public String toString() {
		if (description == null) {
			return "";
		}
		return getName().isEmpty() ? getTooltip() : getName();
	}
}
