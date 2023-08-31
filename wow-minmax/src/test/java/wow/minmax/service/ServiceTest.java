package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import wow.character.model.character.Character;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerProfile;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowMinMaxSpringTest {
	PlayerProfile profile;
	Character character;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		character = profile.getCharacter(CHARACTER_KEY).orElseThrow();
	}

	Spell getSpell(SpellId spellId) {
		return character.getSpellbook().getSpell(spellId).orElseThrow();
	}
}
