package wow.commons.model.effect.impl;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.*;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;

import java.util.List;

import static wow.commons.util.CollectionUtil.join;

/**
 * User: POlszewski
 * Date: 2026-03-24
 */
public class AugmentedEffect implements Effect {
	private final Effect effect;
	private ModifierComponent modifierComponent;
	private List<Attribute> modifierAttributeList;
	private List<StatConversion> statConversions;
	private List<Event> events;

	public AugmentedEffect(Effect effect, EffectAugmentations augmentations) {
		this.effect = effect;
		this.modifierComponent = effect.getModifierComponent();
		this.modifierAttributeList = effect.getModifierAttributeList();
		this.statConversions = effect.getStatConversions();
		this.events = effect.getEvents();
		augment(augmentations);
	}

	@Override
	public EffectId getId() {
		return effect.getId();
	}

	@Override
	public EffectCategory getCategory() {
		return effect.getCategory();
	}

	@Override
	public EffectSource getSource() {
		return effect.getSource();
	}

	@Override
	public List<AbilityId> getAugmentedAbilities() {
		return effect.getAugmentedAbilities();
	}

	@Override
	public int getMaxStacks() {
		return effect.getMaxStacks();
	}

	@Override
	public int getMaxCounters() {
		return effect.getMaxCounters();
	}

	@Override
	public EffectScope getScope() {
		return effect.getScope();
	}

	@Override
	public EffectExclusionGroup getExclusionGroup() {
		return effect.getExclusionGroup();
	}

	@Override
	public PeriodicComponent getPeriodicComponent() {
		return effect.getPeriodicComponent();
	}

	@Override
	public ModifierComponent getModifierComponent() {
		return modifierComponent;
	}

	@Override
	public List<Attribute> getModifierAttributeList() {
		return modifierAttributeList;
	}

	@Override
	public AbsorptionComponent getAbsorptionComponent() {
		return effect.getAbsorptionComponent();
	}

	@Override
	public List<SpellSchool> getPreventedSchools() {
		return effect.getPreventedSchools();
	}

	@Override
	public List<StatConversion> getStatConversions() {
		return statConversions;
	}

	@Override
	public List<Event> getEvents() {
		return events;
	}

	@Override
	public Description getDescription() {
		return effect.getDescription();
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return effect.getTimeRestriction();
	}

	@Override
	public Effect augment(EffectAugmentations augmentations) {
		if (!augmentations.isEmpty()) {
			addModifiers(augmentations.modifiers());
			addStatConversions(augmentations.statConversions());
			addEvents(augmentations.events());
			addEffectIncrease(augmentations.effectIncreasePct());
		}

		return this;
	}

	private void addModifiers(List<Attribute> extraModifiers) {
		setModifier(join(modifierAttributeList, extraModifiers));
	}

	private void setModifier(List<Attribute> modifierAttributeList) {
		this.modifierAttributeList = modifierAttributeList;
		this.modifierComponent = new ModifierComponent(Attributes.of(modifierAttributeList));
	}

	private void addStatConversions(List<StatConversion> extraStatConversions) {
		this.statConversions = join(statConversions, extraStatConversions);
	}

	private void addEvents(List<Event> extraEvents) {
		this.events = join(events, extraEvents);
	}

	private void addEffectIncrease(double effectIncreasePct) {
		setModifier(getScaledAttributes(effectIncreasePct));
	}

	private List<Attribute> getScaledAttributes(double effectIncreasePct) {
		if (effectIncreasePct == 0 || modifierAttributeList == null) {
			return modifierAttributeList;
		}

		var factor = 1 + effectIncreasePct / 100.0;

		return modifierAttributeList.stream()
				.map(x -> x.intScale(factor))
				.toList();
	}

	@Override
	public String toString() {
		return effect.toString();
	}
}
