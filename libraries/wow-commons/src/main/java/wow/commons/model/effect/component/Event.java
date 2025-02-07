package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.spell.Spell;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-17
 */
public record Event(
		List<EventType> types,
		AttributeCondition condition,
		Percent chance,
		List<EventAction> actions,
		Spell triggeredSpell,
		EventActionParameters actionParameters
) implements EffectComponent {
	public Event {
		Objects.requireNonNull(types);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(chance);
		Objects.requireNonNull(actions);
	}

	public Event setTriggeredSpell(Spell triggeredSpell) {
		return new Event(types, condition, chance, actions, triggeredSpell, actionParameters);
	}
}
