package wow.commons.model.attributes.condition;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.character.CreatureType;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record CreatureTypeCondition(CreatureType creatureType) implements AttributeCondition {
	public CreatureTypeCondition {
		Objects.requireNonNull(creatureType);
	}

	public static CreatureTypeCondition of(CreatureType creatureType) {
		return CACHE.get(creatureType);
	}

	@Override
	public String getConditionString() {
		return "creature: " + creatureType;
	}

	@Override
	public String toString() {
		return creatureType.toString();
	}

	private static final Map<CreatureType, CreatureTypeCondition> CACHE = EnumUtil.cache(
			CreatureType.class, CreatureType.values(), CreatureTypeCondition::new);
}
