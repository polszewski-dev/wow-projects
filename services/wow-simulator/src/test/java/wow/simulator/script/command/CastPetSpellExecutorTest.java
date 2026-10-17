package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.util.TestEvent;

import static wow.test.commons.AbilityNames.LASH_OF_PAIN;
import static wow.test.commons.AbilityNames.SUMMON_SUCCUBUS;

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
}