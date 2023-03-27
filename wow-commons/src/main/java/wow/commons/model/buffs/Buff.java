package wow.commons.model.buffs;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.spells.SpellId;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
public interface Buff extends ConfigurationElementWithAttributes<Integer> {
	BuffType getType();

	BuffExclusionGroup getExclusionGroup();

	SpellId getSourceSpell();

	default Attributes modifyEffectByPct(Percent effectIncreasePct) {
		return getAttributes().scale(effectIncreasePct.toMultiplier());
	}
}
