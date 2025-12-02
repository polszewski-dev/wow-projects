package wow.commons.model.spell;

import wow.commons.model.Duration;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.component.DirectComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;
import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2021-09-19
 */
public interface Spell extends Described, TimeRestricted {
	SpellId getId();

	SpellType getType();

	SpellSchool getSchool();

	Duration getCooldown();

	boolean isBolt();

	DirectComponent getDirectComponent();

	default List<DirectCommand> getDirectCommands() {
		var directComponent = getDirectComponent();

		return directComponent != null ? directComponent.commands() : List.of();
	}

	EffectApplication getEffectApplication();

	default Effect getAppliedEffect() {
		return getEffectApplication() != null ? getEffectApplication().effect() : null;
	}

	default List<PeriodicCommand> getPeriodicCommands() {
		var appliedEffect = getAppliedEffect();

		return appliedEffect != null ? appliedEffect.getPeriodicCommands() : List.of();
	}

	default Set<SpellTarget> getTargets() {
		var result = new HashSet<SpellTarget>();

		for (var command : getDirectCommands()) {
			result.add(command.target());
		}

		var effectApplication = getEffectApplication();

		if (effectApplication != null) {
			result.add(effectApplication.target());
		}

		return result;
	}

	boolean hasDamagingComponent();

	boolean hasHealingComponent();

	boolean hasPeriodicComponent(ComponentType componentType);

	default boolean hasCooldown() {
		return getCooldown().isPositive();
	}
}
