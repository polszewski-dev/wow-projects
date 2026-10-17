package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.util.TestEvent;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class CastSpellExecutorTest extends CommandExecutorTest {
	@Test
	void cod_cast_when_available() {
		simulate("/script/command/cod.txt", 120);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, CURSE_OF_DOOM),
				at(60).beginCast(player, CURSE_OF_DOOM)
		);
	}

	@Test
	void coa_cast_when_available() {
		simulate("/script/command/coa.txt", 90);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, CURSE_OF_AGONY),
				at(24).beginCast(player, CURSE_OF_AGONY),
				at(48).beginCast(player, CURSE_OF_AGONY)
		);
	}

	@Test
	void corruption_cast_when_available() {
		simulate("/script/command/corruption.txt", 76);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, CORRUPTION, 2),
				at(18).beginCast(player, CORRUPTION, 2),
				at(36).beginCast(player, CORRUPTION, 2),
				at(54).beginCast(player, CORRUPTION, 2)
		);
	}

	@Test
	void immolate_cast_when_available() {
		simulate("/script/command/immolate.txt", 62);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, IMMOLATE, 2),
				at(15).beginCast(player, IMMOLATE, 2),
				at(30).beginCast(player, IMMOLATE, 2),
				at(45).beginCast(player, IMMOLATE, 2)
		);
	}

	@Test
	void shadow_bolt_cast_when_available() {
		simulate("/script/command/shadow-bolt.txt", 12);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, SHADOW_BOLT, 3),
				at(3).beginCast(player, SHADOW_BOLT, 3),
				at(6).beginCast(player, SHADOW_BOLT, 3),
				at(9).beginCast(player, SHADOW_BOLT, 3)
		);
	}
}