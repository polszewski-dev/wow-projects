package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.util.TestEvent;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.categorization.ItemSlot.TRINKET_2;
import static wow.test.commons.AbilityNames.SCRYERS_BLOODGEM;
import static wow.test.commons.AbilityNames.SHIFTING_NAARU_SLIVER;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class UseItemExecutorTest extends CommandExecutorTest {
	@Test
	void trinket1_used_when_available() {
		simulate("/script/command/trinket-1.txt", 180);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, SCRYERS_BLOODGEM),
				at(90).beginCast(player, SCRYERS_BLOODGEM),
				at(180).beginCast(player, SCRYERS_BLOODGEM)
		);
	}

	@Test
	void both_trinkets_used_when_available() {
		simulate("/script/command/trinket-1-and-2.txt", 180);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, SCRYERS_BLOODGEM),
				at(15).beginCast(player, SHIFTING_NAARU_SLIVER),
				at(90).beginCast(player, SCRYERS_BLOODGEM),
				at(105).beginCast(player, SHIFTING_NAARU_SLIVER),
				at(180).beginCast(player, SCRYERS_BLOODGEM)
		);
	}

	@Override
	protected void afterSetUp() {
		super.afterSetUp();

		equip(SCRYERS_BLOODGEM, TRINKET_1);
		equip(SHIFTING_NAARU_SLIVER, TRINKET_2);
	}
}