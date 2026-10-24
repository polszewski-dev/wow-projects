package wow.simulator.simulation.script.tbc.shaman;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class ShamanScriptTest extends TbcShamanSpellSimulationTest {
	@Test
	void lightning_bolt_spam() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/shaman/55003105100213351051--05105301005");

		setScript("shaman-lightning-bolt-spam");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, LIGHTNING_BOLT, 2),
				beginCast(62, LIGHTNING_BOLT, 2),
				beginCast(64, LIGHTNING_BOLT, 2),
				beginCast(66, LIGHTNING_BOLT, 2),
				beginCast(68, LIGHTNING_BOLT, 2),
				beginCast(70, LIGHTNING_BOLT, 2),
				beginCast(72, LIGHTNING_BOLT, 2),
				beginCast(74, LIGHTNING_BOLT, 2),
				beginCast(76, LIGHTNING_BOLT, 2),
				beginCast(78, LIGHTNING_BOLT, 2),
				beginCast(80, LIGHTNING_BOLT, 2),
				beginCast(82, LIGHTNING_BOLT, 2),
				beginCast(84, LIGHTNING_BOLT, 2),
				beginCast(86, LIGHTNING_BOLT, 2),
				beginCast(88, LIGHTNING_BOLT, 2),
				beginCast(90, LIGHTNING_BOLT, 2),
				beginCast(92, LIGHTNING_BOLT, 2),
				beginCast(94, LIGHTNING_BOLT, 2),
				beginCast(96, LIGHTNING_BOLT, 2),
				beginCast(98, LIGHTNING_BOLT, 2),
				beginCast(100, LIGHTNING_BOLT, 2),
				beginCast(102, LIGHTNING_BOLT, 2),
				beginCast(104, LIGHTNING_BOLT, 2),
				beginCast(106, LIGHTNING_BOLT, 2),
				beginCast(108, LIGHTNING_BOLT, 2),
				beginCast(110, LIGHTNING_BOLT, 2),
				beginCast(112, LIGHTNING_BOLT, 2),
				beginCast(114, LIGHTNING_BOLT, 2),
				beginCast(116, LIGHTNING_BOLT, 2),
				beginCast(118, LIGHTNING_BOLT, 2)
		);
	}
}
