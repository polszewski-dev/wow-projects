package wow.commons.model.attribute.condition;

import wow.commons.model.character.CreatureType;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record TargetTypeCondition(CreatureType creatureType) implements AttributeCondition {
	public TargetTypeCondition {
		Objects.requireNonNull(creatureType);
	}

	public static TargetTypeCondition of(CreatureType creatureType) {
		return CACHE.get(creatureType);
	}

	@Override
	public String toString() {
		return creatureType.toString();
	}

	private static final Map<CreatureType, TargetTypeCondition> CACHE = EnumUtil.cache(
			CreatureType.class, CreatureType.values(), TargetTypeCondition::new);
}
