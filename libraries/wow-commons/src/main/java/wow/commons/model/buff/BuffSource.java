package wow.commons.model.buff;

import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record BuffSource(Description description) implements EffectSource, Comparable<BuffSource> {
	@Override
	public Description getDescription() {
		return description;
	}

	@Override
	public int getPriority() {
		return 4;
	}

	@Override
	public int compareTo(BuffSource o) {
		return this.getName().compareTo(o.getName());
	}
}
