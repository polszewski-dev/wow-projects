package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
@AllArgsConstructor
@Getter
public class Racial implements Described {
	@NonNull
	private final Description description;

	@NonNull
	private final Race race;

	@NonNull
	private final Set<CharacterClassId> requiredClasses;

	public boolean matches(CharacterClassId characterClassId) {
		return requiredClasses.isEmpty() || requiredClasses.contains(characterClassId);
	}
}
