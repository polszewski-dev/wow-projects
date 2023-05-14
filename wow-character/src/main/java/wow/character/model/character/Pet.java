package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.character.PetType;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-05-15
 */
@AllArgsConstructor
@Getter
public class Pet implements Described {
	@NonNull
	private final PetType petType;

	@NonNull
	private final Description description;

	@NonNull
	private final GameVersion gameVersion;

	public Set<AttributeCondition> getConditions() {
		return Set.of(AttributeCondition.of(petType));
	}

	@Override
	public String toString() {
		return getName();
	}
}
