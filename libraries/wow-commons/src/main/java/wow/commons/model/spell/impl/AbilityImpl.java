package wow.commons.model.spell.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.spell.*;

/**
 * User: POlszewski
 * Date: 2023-10-04
 */
@Getter
@Setter
public abstract class AbilityImpl extends SpellImpl implements Ability {
	private AbilityId abilityId;
	private AbilityCategory category;
	private CastInfo castInfo;
	private int range;
	private AbilityId effectRemovedOnHit;
	private CharacterRestriction characterRestriction;
	private AbilityNameRank nameRank;

	public void setNameRank(int rank) {
		this.nameRank = new AbilityNameRank(getName(), rank);
	}

	@Override
	public String toString() {
		return nameRank.toString();
	}
}
