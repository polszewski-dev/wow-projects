package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerProfile;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowMinMaxSpringTest {
	PlayerProfile profile;
	PlayerCharacter character;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		character = profile.getCharacter(CHARACTER_KEY).orElseThrow();
	}

	Ability getAbility(AbilityId abilityId) {
		return character.getAbility(abilityId).orElseThrow();
	}
}
