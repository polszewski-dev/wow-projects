package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.util.TestEvent;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class CastPetSpellExecutorTest extends CommandExecutorTest {
	@Test
	void pet_spell_cast_when_available() {
		player.setActive();
		player.cast(SUMMON_SUCCUBUS);

		simulate("/script/command/lash-of-pain.txt", 40);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, SUMMON_SUCCUBUS, 10),
				at(10).beginCast(pet, LASH_OF_PAIN),
				at(22).beginCast(pet, LASH_OF_PAIN),
				at(34).beginCast(pet, LASH_OF_PAIN)
		);
	}

	@Test
	void pet_spell_not_cast_when_not_available_to_the_pet() {
		player.setActive();
		player.cast(SUMMON_IMP);

		simulate("/script/command/lash-of-pain.txt", 40);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, SUMMON_IMP, 10)
		);
	}

	@Test
	void pet_spell_not_cast_without_pet() {
		simulate("/script/command/lash-of-pain.txt", 40);

		assertEvents(
				TestEvent::isBeginCast
		);
	}
}