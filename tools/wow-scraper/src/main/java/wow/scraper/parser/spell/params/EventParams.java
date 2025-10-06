package wow.scraper.parser.spell.params;

import wow.commons.model.effect.component.EventAction;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.spell.SpellTarget;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public record EventParams(
		SpellTarget target,
		List<EventType> types,
		EventCondition condition,
		String chancePct,
		List<EventAction> actions,
		String cooldown,
		SpellPatternParams triggeredSpell
) {
	public EventParams {
		Objects.requireNonNull(target);
		Objects.requireNonNull(types);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(actions);
	}

	public EventParams setTriggeredSpell(SpellPatternParams triggeredSpell) {
		if (this.triggeredSpell != null) {
			throw new IllegalArgumentException("Triggered spell is already defined");
		}
		return new EventParams(target, types, condition, chancePct, actions, cooldown, triggeredSpell);
	}
}
