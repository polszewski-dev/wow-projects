package wow.commons.model.attribute.condition;

import wow.commons.model.character.PetType;
import wow.commons.util.EnumUtil;

import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record PetTypeCondition(PetType petType) implements AttributeCondition {
	public PetTypeCondition {
		Objects.requireNonNull(petType);
	}

	public static PetTypeCondition of(PetType petType) {
		return CACHE.get(petType);
	}

	@Override
	public String getConditionString() {
		return "pet: " + petType;
	}

	@Override
	public String toString() {
		return petType.toString();
	}

	private static final Map<PetType, PetTypeCondition> CACHE = EnumUtil.cache(
			PetType.class, PetType.values(), PetTypeCondition::new);
}
