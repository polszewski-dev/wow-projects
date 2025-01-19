package wow.commons.model.effect.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.AbilityId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
@Getter
@Setter
public class EffectImpl implements Effect {
	private int effectId;
	private EffectCategory category;
	private EffectSource source;
	private final AbilityId augmentedAbility;
	private Description description;
	private TimeRestriction timeRestriction;
	private int maxStacks;
	private PeriodicComponent periodicComponent;
	private Duration tickInterval;
	private ModifierComponent modifierComponent;
	private List<Attribute> modifierAttributeList;
	private AbsorptionComponent absorptionComponent;
	private List<StatConversion> statConversions;
	private EffectIncreasePerEffectOnTarget effectIncreasePerEffectOnTarget;
	private List<Event> events;

	public EffectImpl(AbilityId augmentedAbility) {
		this.augmentedAbility = augmentedAbility;
	}

	public static EffectImpl newAttributeEffect(Attributes attributes) {
		return newAttributeEffect(null, attributes, null);
	}

	public static EffectImpl newAttributeEffect(Attributes attributes, String tooltip) {
		return newAttributeEffect(null, attributes, tooltip);
	}

	public static EffectImpl newAttributeEffect(AbilityId augmentedAbility, Attributes attributes, String tooltip) {
		var effect = new EffectImpl(augmentedAbility);
		effect.setModifierComponent(new ModifierComponent(attributes));
		if (tooltip != null) {
			effect.setDescription(new Description("", null, tooltip));
		}
		effect.setStatConversions(List.of());
		effect.setEvents(List.of());
		return effect;
	}

	public void attachSource(EffectSource source) {
		this.source = source;
	}

	public void setModifierComponent(ModifierComponent modifierComponent) {
		this.modifierComponent = modifierComponent;
		if (modifierComponent != null) {
			this.modifierAttributeList = modifierComponent.attributes().list();
		}
	}

	@Override
	public String toString() {
		if (description == null) {
			return "";
		}
		return getName().isEmpty() ? getTooltip() : getName();
	}
}
