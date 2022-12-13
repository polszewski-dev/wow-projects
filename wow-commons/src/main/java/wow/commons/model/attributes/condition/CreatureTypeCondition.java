package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.character.CreatureType;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class CreatureTypeCondition implements AttributeCondition {
	@NonNull
	private final CreatureType creatureType;

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
