package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.spell.*;

/**
 * User: POlszewski
 * Date: 2023-10-04
 */
@Getter
@Setter
public abstract class AbilityImpl extends SpellImpl implements Ability {
	private AbilityCategory category;
	private CastInfo castInfo;
	private Duration cooldown;
	private int range;
	private AbilityId requiredEffect;
	private AbilityId effectRemovedOnHit;
	private CharacterRestriction characterRestriction;
	private AbilityIdAndRank rankedAbilityId;

	public void setRankedAbilityId(int rank) {
		this.rankedAbilityId = new AbilityIdAndRank(AbilityId.parse(getName()), rank);
	}

	@Override
	public String toString() {
		return getRankedAbilityId().toString();
	}
}
