package wow.commons.model.buffs;

import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record BuffSource(Buff buff) implements SpecialAbilitySource, Comparable<BuffSource> {
	@Override
	public Description getDescription() {
		return buff.getDescription();
	}

	@Override
	public int getPriority() {
		return 4;
	}

	@Override
	public int compareTo(BuffSource o) {
		return this.buff.getId().compareTo(o.buff.getId());
	}
}
