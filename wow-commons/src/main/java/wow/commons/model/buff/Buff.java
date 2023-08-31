package wow.commons.model.buff;

import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.spell.SpellId;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
public interface Buff extends ConfigurationElementWithAttributes<BuffIdAndRank>, PveRoleClassified {
	default BuffId getBuffId() {
		return getId().buffId();
	}

	default Integer getRank() {
		return getId().rank();
	}

	BuffType getType();

	BuffExclusionGroup getExclusionGroup();

	SpellId getSourceSpell();

	Set<BuffCategory> getCategories();

	default boolean isDebuff() {
		return getType() == BuffType.DEBUFF;
	}
}
