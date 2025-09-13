package wow.commons.model.talent;

import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record TalentSource(Talent talent) implements EffectSource, Comparable<TalentSource> {
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
		return this.talent.getName().compareTo(o.talent.getName());
	}
}
