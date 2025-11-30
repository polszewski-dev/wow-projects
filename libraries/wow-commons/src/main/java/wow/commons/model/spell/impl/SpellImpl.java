package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.EffectApplication;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.component.DirectComponent;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@Setter
public abstract class SpellImpl implements Spell {
	private SpellId id;
	private Description description;
	private TimeRestriction timeRestriction;
	private SpellSchool school;
	private Duration cooldown;
	private DirectComponent directComponent;
	private EffectApplication effectApplication;
	private boolean hasDamagingComponent;
	private boolean hasHealingComponent;

	@Override
	public boolean hasDamagingComponent() {
		return hasDamagingComponent;
	}

	@Override
	public boolean hasHealingComponent() {
		return hasHealingComponent;
	}

	@Override
	public boolean hasPeriodicComponent(ComponentType componentType) {
		if (effectApplication == null) {
			return false;
		}

		var effect = effectApplication.effect();

		return effect != null && effect.hasPeriodicComponent(componentType);
	}

	@Override
	public String toString() {
		return getName();
	}
}
