package wow.commons.model.attributes.complex.special.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.buffs.Buff;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class BuffSource implements SpecialAbilitySource, Comparable<BuffSource> {
	private final Buff buff;

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
