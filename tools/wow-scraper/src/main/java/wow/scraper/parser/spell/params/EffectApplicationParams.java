package wow.scraper.parser.spell.params;

import java.util.Collection;
import java.util.Objects;

import static wow.commons.model.effect.component.EventAction.*;

/**
 * User: POlszewski
 * Date: 2023-09-28
 */
public record EffectApplicationParams(
		EffectPatternParams effect,
		String duration,
		String numCharges,
		String numStacks
) {
	public EffectApplicationParams {
		Objects.requireNonNull(effect);
		Objects.requireNonNull(duration);
		validateEvents(effect, numCharges, numStacks);
	}

	private static void validateEvents(EffectPatternParams effect, String numCharges, String numStacks) {
		var actionTypes = effect.getEvents().stream()
				.map(EventParams::actions)
				.flatMap(Collection::stream)
				.toList();

		if (actionTypes.contains(ADD_STACK) || actionTypes.contains(REMOVE_STACK)) {
			Objects.requireNonNull(numStacks, "No #stacks specified");
			Objects.requireNonNull(effect.maxStacks(), "No max stacks specified");
		}

		if (actionTypes.contains(REMOVE_CHARGE)) {
			Objects.requireNonNull(numCharges, "No #charges specified");
		}
	}
}
