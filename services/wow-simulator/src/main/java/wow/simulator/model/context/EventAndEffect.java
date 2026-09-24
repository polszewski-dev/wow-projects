package wow.simulator.model.context;

import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2026-09-24
 */
public record EventAndEffect(Event event, Effect effect, Unit effectTarget) {
}
