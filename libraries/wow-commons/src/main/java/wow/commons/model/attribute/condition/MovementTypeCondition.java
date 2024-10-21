package wow.commons.model.attribute.condition;

import wow.commons.model.character.MovementType;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-10-15
 */
public record MovementTypeCondition(MovementType type) implements AttributeCondition {
	public MovementTypeCondition {
		Objects.requireNonNull(type);
	}

	public static MovementTypeCondition of(MovementType type) {
		return CACHE.get(type);
	}

	@Override
	public String toString() {
		return type.toString();
	}

	private static final Map<MovementType, MovementTypeCondition> CACHE = EnumUtil.cache(
			MovementType.class, MovementType.values(), MovementTypeCondition::new);
}
