package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.spells.SpellId;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SpellIdCondition implements AttributeCondition {
	@NonNull
	private final SpellId spellId;

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
