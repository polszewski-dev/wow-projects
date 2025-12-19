package wow.character.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.character.PetType;

/**
 * User: POlszewski
 * Date: 2025-12-19
 */
@RequiredArgsConstructor
@Getter
@Setter
public class SpellTargetConditionArgs {
	private final Character caster;
	private final Character target;
	private PetType sacrificedPetType;
}
