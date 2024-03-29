package wow.commons.model.buff.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.buff.*;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.AbilityId;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@Setter
public class BuffImpl implements Buff {
	private final BuffIdAndRank id;
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final BuffType type;
	private final BuffExclusionGroup exclusionGroup;
	private final Set<PveRole> pveRoles;
	private final Set<BuffCategory> categories;
	private Effect effect;

	public BuffImpl(
			BuffIdAndRank id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BuffType type,
			BuffExclusionGroup exclusionGroup,
			Set<PveRole> pveRoles,
			Set<BuffCategory> categories
	) {
		this.id = id;
		this.description = description;
		this.timeRestriction = timeRestriction;
		this.characterRestriction = characterRestriction;
		this.type = type;
		this.exclusionGroup = exclusionGroup;
		this.pveRoles = pveRoles;
		this.categories = categories;
	}

	@Override
	public AbilityId getSourceSpell() {
		return getCharacterRestriction().abilityId();
	}

	@Override
	public String toString() {
		return getName();
	}
}
