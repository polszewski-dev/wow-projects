package wow.simulator.simulation.script.vanilla.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.vanilla.VanillaMageSpellSimulationTest;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class MageScriptTest extends VanillaMageSpellSimulationTest {
	@Test
	void fireball_spam() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/mage/230025030002-5052020023033051-003");

		setScript("mage-fireball-spam");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, COMBUSTION),
				beginCast(60, FIREBALL, 3),
				beginCast(63, FIREBALL, 3),
				beginCast(66, FIREBALL, 3),
				beginCast(69, FIREBALL, 3),
				beginCast(72, FIREBALL, 3),
				beginCast(75, FIREBALL, 3),
				beginCast(78, FIREBALL, 3),
				beginCast(81, FIREBALL, 3),
				beginCast(84, FIREBALL, 3),
				beginCast(87, FIREBALL, 3),
				beginCast(90, FIREBALL, 3),
				beginCast(93, FIREBALL, 3),
				beginCast(96, FIREBALL, 3),
				beginCast(99, FIREBALL, 3),
				beginCast(102, FIREBALL, 3),
				beginCast(105, FIREBALL, 3),
				beginCast(108, FIREBALL, 3),
				beginCast(111, FIREBALL, 3),
				beginCast(114, FIREBALL, 3),
				beginCast(117, FIREBALL, 3),
				beginCast(120, FIRE_BLAST)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Doomfinger");
	}
}
