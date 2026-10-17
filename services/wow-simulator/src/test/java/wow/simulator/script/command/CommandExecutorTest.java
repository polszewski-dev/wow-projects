package wow.simulator.script.command;

import wow.commons.model.Duration;
import wow.simulator.model.unit.impl.UnitImpl;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.simulator.constant.HiddenEffectNames.BONUS_HP5;
import static wow.simulator.constant.HiddenEffectNames.BONUS_MP5;

/**
 * User: POlszewski
 * Date: 2025-09-20
 */
abstract class CommandExecutorTest extends TbcWarlockSpellSimulationTest {
	void simulate(String script, double time) {
		player.setScript(script);
		player.setActive();

		player.addHiddenEffect(BONUS_HP5, 5000);
		player.addHiddenEffect(BONUS_MP5, 5000);

		updateUntil(time);
	}

	@Override
	protected void afterSetUp() {
		((UnitImpl) player).setIdleUntilNextActionDuration(Duration.seconds(0.5));
	}
}
