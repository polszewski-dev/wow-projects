package wow.commons.model.racial;

import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record RacialSource(Racial racial) implements EffectSource, Comparable<RacialSource> {
	@Override
	public Description getDescription() {
		return racial.getDescription();
	}

	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public int compareTo(RacialSource o) {
		return this.racial.getName().compareTo(o.racial.getName());
	}
}
