package wow.commons.model.attributes.condition;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.character.PetType;
import wow.commons.util.EnumUtil;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class PetTypeCondition implements AttributeCondition {
	@NonNull
	private final PetType petType;

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
