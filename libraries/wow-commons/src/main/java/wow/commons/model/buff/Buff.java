package wow.commons.model.buff;

import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
public interface Buff extends Described, TimeRestricted, CharacterRestricted, PveRoleClassified {
	BuffId getId();

	BuffNameRank getNameRank();

	default int getRank() {
		return getNameRank().rank();
	}

	BuffType getType();

	BuffExclusionGroup getExclusionGroup();

	AbilityId getSourceSpell();

	Set<BuffCategory> getCategories();

	default boolean isDebuff() {
		return getType() == BuffType.DEBUFF;
	}

	Effect getEffect();

	int getStacks();

	default boolean isSchoolPrevented(SpellSchool school) {
		return getEffect().isSchoolPrevented(school);
	}
}
