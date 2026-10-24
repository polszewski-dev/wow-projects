package wow.simulator.simulation.script.tbc.druid;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.test.commons.AbilityNames.WRATH;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class DruidScriptTest extends TbcDruidSpellSimulationTest {
	@Test
	void wrath_spam() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/druid/510022312503135231351--500233");

		setScript("druid-wrath-spam");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, WRATH, 1.5),
				beginCast(61.5, WRATH, 1.5),
				beginCast(63, WRATH, 1.5),
				beginCast(64.5, WRATH, 1.5),
				beginCast(66, WRATH, 1.5),
				beginCast(67.5, WRATH, 1.5),
				beginCast(69, WRATH, 1.5),
				beginCast(70.5, WRATH, 1.5),
				beginCast(72, WRATH, 1.5),
				beginCast(73.5, WRATH, 1.5),
				beginCast(75, WRATH, 1.5),
				beginCast(76.5, WRATH, 1.5),
				beginCast(78, WRATH, 1.5),
				beginCast(79.5, WRATH, 1.5),
				beginCast(81, WRATH, 1.5),
				beginCast(82.5, WRATH, 1.5),
				beginCast(84, WRATH, 1.5),
				beginCast(85.5, WRATH, 1.5),
				beginCast(87, WRATH, 1.5),
				beginCast(88.5, WRATH, 1.5),
				beginCast(90, WRATH, 1.5),
				beginCast(91.5, WRATH, 1.5),
				beginCast(93, WRATH, 1.5),
				beginCast(94.5, WRATH, 1.5),
				beginCast(96, WRATH, 1.5),
				beginCast(97.5, WRATH, 1.5),
				beginCast(99, WRATH, 1.5),
				beginCast(100.5, WRATH, 1.5),
				beginCast(102, WRATH, 1.5),
				beginCast(103.5, WRATH, 1.5),
				beginCast(105, WRATH, 1.5),
				beginCast(106.5, WRATH, 1.5),
				beginCast(108, WRATH, 1.5),
				beginCast(109.5, WRATH, 1.5),
				beginCast(111, WRATH, 1.5),
				beginCast(112.5, WRATH, 1.5),
				beginCast(114, WRATH, 1.5),
				beginCast(115.5, WRATH, 1.5),
				beginCast(117, WRATH, 1.5),
				beginCast(118.5, WRATH, 1.5)
		);
	}
}
