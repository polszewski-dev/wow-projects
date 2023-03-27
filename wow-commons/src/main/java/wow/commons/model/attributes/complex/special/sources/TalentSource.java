package wow.commons.model.attributes.complex.special.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;
import wow.commons.model.talents.Talent;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class TalentSource implements SpecialAbilitySource, Comparable<TalentSource> {
	private final Talent talent;

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
