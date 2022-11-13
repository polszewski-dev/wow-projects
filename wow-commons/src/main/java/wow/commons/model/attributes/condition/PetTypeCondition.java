package wow.commons.model.attributes.condition;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.unit.PetType;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@AllArgsConstructor
@EqualsAndHashCode
public class PetTypeCondition implements AttributeCondition {
	@NonNull
	private final PetType petType;

	@Override
	public String toString() {
		return "pet: " + petType;
	}
}
