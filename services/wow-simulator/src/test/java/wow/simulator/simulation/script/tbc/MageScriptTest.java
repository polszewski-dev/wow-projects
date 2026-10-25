package wow.simulator.simulation.script.tbc;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.AssetNames;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class MageScriptTest extends TbcMageSpellSimulationTest {
	@Test
	void fireball_spam() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/mage/2-5052120123033310531251-053002001");

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

	@Test
	void scorch_plus_fireball() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/mage/2-5052120123033310531251-053002001");
		enableAsset(AssetNames.IMPROVED_SCORCH);

		setScript("mage-scorch-plus-fireball");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, SCORCH, 1.5),
				beginCast(61.5, SCORCH, 1.5),
				beginCast(63, SCORCH, 1.5),
				beginCast(64.5, SCORCH, 1.5),
				beginCast(66, SCORCH, 1.5),
				beginCast(67.5, COMBUSTION),
				beginCast(67.5, FIREBALL, 3),
				beginCast(70.5, FIREBALL, 3),
				beginCast(73.5, FIREBALL, 3),
				beginCast(76.5, FIREBALL, 3),
				beginCast(79.5, FIREBALL, 3),
				beginCast(82.5, FIREBALL, 3),
				beginCast(85.5, FIREBALL, 3),
				beginCast(88.5, FIREBALL, 3),
				beginCast(91.5, FIREBALL, 3),
				beginCast(94.5, SCORCH, 1.5),
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

	@Test
	void scorch_plus_fireball_2_mages() {
		enableTalents(player, "https://www.wowhead.com/tbc/talent-calc/mage/2-5052120123033310531251-053002001");
		enableTalents(player2, "https://www.wowhead.com/tbc/talent-calc/mage/2-5052120123033310531251-053002001");
		enableAsset(player, AssetNames.IMPROVED_SCORCH);
		enableAsset(player2, AssetNames.IMPROVED_SCORCH);

		setScript(player, "mage-scorch-plus-fireball");
		setScript(player2, "mage-scorch-plus-fireball");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, player, SCORCH, 1.5),
				beginCast(60, player2, SCORCH, 1.5),
				beginCast(61.5, player, SCORCH, 1.5),
				beginCast(61.5, player2, SCORCH, 1.5),
				beginCast(63, player, SCORCH, 1.5),
				beginCast(63, player2, SCORCH, 1.5),
				beginCast(64.5, player, COMBUSTION),
				beginCast(64.5, player2, COMBUSTION),
				beginCast(64.5, player, FIREBALL, 3),
				beginCast(64.5, player2, FIREBALL, 3),
				beginCast(67.5, player, FIREBALL, 3),
				beginCast(67.5, player2, FIREBALL, 3),
				beginCast(70.5, player, FIREBALL, 3),
				beginCast(70.5, player2, FIREBALL, 3),
				beginCast(73.5, player, FIREBALL, 3),
				beginCast(73.5, player2, FIREBALL, 3),
				beginCast(76.5, player, FIREBALL, 3),
				beginCast(76.5, player2, FIREBALL, 3),
				beginCast(79.5, player, FIREBALL, 3),
				beginCast(79.5, player2, FIREBALL, 3),
				beginCast(82.5, player, FIREBALL, 3),
				beginCast(82.5, player2, FIREBALL, 3),
				beginCast(85.5, player, FIREBALL, 3),
				beginCast(85.5, player2, FIREBALL, 3),
				beginCast(88.5, player, FIREBALL, 3),
				beginCast(88.5, player2, FIREBALL, 3),
				beginCast(91.5, player, SCORCH, 1.5),
				beginCast(91.5, player2, SCORCH, 1.5),
				beginCast(93, player, FIREBALL, 3),
				beginCast(93, player2, FIREBALL, 3),
				beginCast(96, player, FIREBALL, 3),
				beginCast(96, player2, FIREBALL, 3),
				beginCast(99, player, FIREBALL, 3),
				beginCast(99, player2, FIREBALL, 3),
				beginCast(102, player, FIREBALL, 3),
				beginCast(102, player2, FIREBALL, 3),
				beginCast(105, player, FIREBALL, 3),
				beginCast(105, player2, FIREBALL, 3),
				beginCast(108, player, FIREBALL, 3),
				beginCast(108, player2, FIREBALL, 3),
				beginCast(111, player, FIREBALL, 3),
				beginCast(111, player2, FIREBALL, 3),
				beginCast(114, player, FIREBALL, 3),
				beginCast(114, player2, FIREBALL, 3),
				beginCast(117, player, FIREBALL, 3),
				beginCast(117, player2, FIREBALL, 3),
				beginCast(120, player, FIRE_BLAST),
				beginCast(120, player2, FIRE_BLAST)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Wand of the Forgotten Star");
	}
}
