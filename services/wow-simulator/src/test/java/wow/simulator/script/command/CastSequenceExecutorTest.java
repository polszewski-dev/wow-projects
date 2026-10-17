package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.util.TestEvent;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.AbilityNames.SCRYERS_BLOODGEM;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class CastSequenceExecutorTest extends CommandExecutorTest {
	@Test
	void both_trinket_and_coa_activated_at_the_same_time() {
		simulate("/script/command/trinket-1-followed-by-coa.txt", 120);

		assertEvents(
				TestEvent::isBeginCast,
				at(0)
						.beginCast(player, SCRYERS_BLOODGEM)
						.beginCast(player, CURSE_OF_AGONY),
				at(90)
						.beginCast(player, SCRYERS_BLOODGEM)
						.beginCast(player, CURSE_OF_AGONY)
		);
	}

	@Test
	void coa_activated_when_available_optionally_preceded_by_trinket() {
		simulate("/script/command/optional-trinket-1-followed-by-coa.txt", 120);

		assertEvents(
				TestEvent::isBeginCast,
				at(0)
						.beginCast(player, SCRYERS_BLOODGEM)
						.beginCast(player, CURSE_OF_AGONY),
				at(24)
						.beginCast(player, CURSE_OF_AGONY),
				at(48)
						.beginCast(player, CURSE_OF_AGONY),
				at(72)
						.beginCast(player, CURSE_OF_AGONY),
				at(96)
						.beginCast(player, SCRYERS_BLOODGEM)
						.beginCast(player, CURSE_OF_AGONY)
		);
	}

	@Override
	protected void afterSetUp() {
		super.afterSetUp();

		equip(SCRYERS_BLOODGEM, TRINKET_1);
	}
}