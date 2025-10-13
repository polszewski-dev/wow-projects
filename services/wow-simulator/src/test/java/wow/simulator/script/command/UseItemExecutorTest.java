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

		assertResultAt(0, trinket1Executor::allConditionsAreMet, true);

		trinket1Executor.execute();

		assertResultAt(14, trinket1Executor::allConditionsAreMet, false);
		assertResultAt(15, trinket1Executor::allConditionsAreMet, false);
		assertResultAt(89, trinket1Executor::allConditionsAreMet, false);
		assertResultAt(90, trinket1Executor::allConditionsAreMet, true);

		updateUntil(180);
	}

	@Test
	void allConditionsAreMet_Trinke2_after_Trinket1() {
		var trinket1Executor = getUseItemExecutor(TRINKET_1);
		var trinket2Executor = getUseItemExecutor(TRINKET_2);

		assertResultAt(0, trinket2Executor::allConditionsAreMet, true);

		trinket1Executor.execute();

		player.idleUntil(Time.at(15));

		trinket2Executor.execute();

		assertResultAt(14, trinket2Executor::allConditionsAreMet, false);
		assertResultAt(15, trinket2Executor::allConditionsAreMet, true);
		assertResultAt(16, trinket2Executor::allConditionsAreMet, false);
		assertResultAt(30, trinket2Executor::allConditionsAreMet, false);
		assertResultAt(104, trinket2Executor::allConditionsAreMet, false);
		assertResultAt(105, trinket2Executor::allConditionsAreMet, true);

		updateUntil(180);
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