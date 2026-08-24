package wow.commons.model.spell;

import wow.commons.model.Duration;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.spell.component.DirectComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;
import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;

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

	default List<ApplyEffect> getApplyEffectCommands() {
		var effectApplication = getEffectApplication();

		return effectApplication != null ? effectApplication.commands() : List.of();
	}

	default Set<SpellTarget> getTargets() {
		return getTargets(x -> true, x -> true);
	}

	default Set<SpellTarget> getTargets(Predicate<DirectCommand> directCommandPredicate, Predicate<ApplyEffect> applyEffectPredicate) {
		var result = new HashSet<SpellTarget>();

		for (var command : getDirectCommands()) {
			if (directCommandPredicate.test(command)) {
				result.add(command.target());
			}
		}

		for (var command : getApplyEffectCommands()) {
			if (applyEffectPredicate.test(command)) {
				result.add(command.target());
			}
		}

		return result;
	}

	boolean hasDamagingComponent();

	boolean hasHealingComponent();

	boolean hasDamagingPeriodicComponent();

	default boolean hasCooldown() {
		return getCooldown().isPositive();
	}
}
