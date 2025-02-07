package wow.commons.model.effect;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.StatConversion;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-02-07
 */
public record EffectAugmentations(
		double effectIncreasePct,
		List<Attribute> modifiers,
		List<StatConversion> statConversions,
		List<Event> events
) {
	public static final EffectAugmentations EMPTY = new EffectAugmentations(0, List.of(), List.of(), List.of());

	public EffectAugmentations {
		Objects.requireNonNull(modifiers);
		Objects.requireNonNull(statConversions);
		Objects.requireNonNull(events);
	}
}
