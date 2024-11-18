package wow.commons.model.effect;

import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record RacialSource(Description description) implements EffectSource, Comparable<RacialSource> {
	@Override
	public Description getDescription() {
		return description;
	}

	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public int compareTo(RacialSource o) {
		return this.getName().compareTo(o.getName());
	}
}
