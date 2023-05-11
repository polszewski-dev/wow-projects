package wow.commons.model.buffs;

import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.spells.SpellId;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
public interface Buff extends ConfigurationElementWithAttributes<Integer>, PveRoleClassified {
	BuffType getType();

	BuffExclusionGroup getExclusionGroup();

	SpellId getSourceSpell();

	Set<BuffCategory> getCategories();

	default boolean isDebuff() {
		return getType() == BuffType.DEBUFF;
	}
}
