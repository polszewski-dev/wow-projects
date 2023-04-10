package wow.commons.model.categorization;

import wow.commons.model.config.CharacterInfo;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-04-10
 */
public interface PveRoleClassified {
	Set<PveRole> getPveRoles();

	default boolean isSuitableFor(CharacterInfo character) {
		return getPveRoles().contains(character.getRole());
	}
}
