package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
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
	private boolean bolt;
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
	public boolean hasDamagingPeriodicComponent() {
		return getApplyEffectCommands().stream().anyMatch(
				x -> x.effect().hasDamagingPeriodicComponent()
		);
	}

	@Override
	public String toString() {
		return getName();
	}
}
