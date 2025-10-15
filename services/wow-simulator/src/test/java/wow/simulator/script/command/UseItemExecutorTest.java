package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.categorization.ItemSlot.TRINKET_2;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class UseItemExecutorTest extends CommandExecutorTest {
	@Test
	void allConditionsAreMet_Trinket1() {
		var trinket1Executor = getUseItemExecutor(TRINKET_1);

		snapshotAt(trinket1Executor, 0, 14, 15, 89, 90);

		trinket1Executor.execute();

		updateUntil(180);

		assertResultAt(0, true);
		assertResultAt(14, false);
		assertResultAt(15, false);
		assertResultAt(89, false);
		assertResultAt(90, true);
	}

	@Test
	void allConditionsAreMet_Trinke2_after_Trinket1() {
		var trinket1Executor = getUseItemExecutor(TRINKET_1);
		var trinket2Executor = getUseItemExecutor(TRINKET_2);

		snapshotAt(trinket2Executor, 0, 14, 15, 16, 30, 104, 105);

		trinket1Executor.execute();

		player.idleUntil(Time.at(15));

		trinket2Executor.execute();

		updateUntil(180);

		assertResultAt(0, true);
		assertResultAt(14, false);
		assertResultAt(15, true);
		assertResultAt(16, false);
		assertResultAt(30, false);
		assertResultAt(104, false);
		assertResultAt(105, true);
	}

	@Override
	protected void afterSetUp() {
		super.afterSetUp();

		equip(SCRYERS_BLOODGEM, TRINKET_1);
		equip(SHIFTING_NAARU_SLIVER, TRINKET_2);
	}

	static final String SCRYERS_BLOODGEM = "Scryer's Bloodgem";
	static final String SHIFTING_NAARU_SLIVER = "Shifting Naaru Sliver";
}