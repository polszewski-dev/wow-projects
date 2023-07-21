package wow.commons.model.attributes.condition;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.spells.SpellId;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record SpellIdCondition(SpellId spellId) implements AttributeCondition {
	public SpellIdCondition {
		Objects.requireNonNull(spellId);
	}

	public static SpellIdCondition of(SpellId spellId) {
		return CACHE.get(spellId);
	}

	@Override
	public String getConditionString() {
		return "spell: " + spellId;
	}

	@Override
	public String toString() {
		return spellId.toString();
	}

	private static final Map<SpellId, SpellIdCondition> CACHE = EnumUtil.cache(
			SpellId.class, SpellId.values(), SpellIdCondition::new);
}
