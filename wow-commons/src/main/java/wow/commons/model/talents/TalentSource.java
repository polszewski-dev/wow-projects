package wow.commons.model.talents;

import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record TalentSource(Talent talent) implements SpecialAbilitySource, Comparable<TalentSource> {
	@Override
	public Description getDescription() {
		return talent.getDescription();
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public int compareTo(TalentSource o) {
		return this.talent.getTalentId().compareTo(o.talent.getTalentId());
	}
}
