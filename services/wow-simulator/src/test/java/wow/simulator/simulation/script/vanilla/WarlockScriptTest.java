package wow.simulator.simulation.script.vanilla;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.vanilla.VanillaWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class WarlockScriptTest extends VanillaWarlockSpellSimulationTest {
	@Test
	void shadow_bolt_spam() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/warlock/25002-2050300142301-52500051020001");

		setScript("warlock-shadow-bolt-spam");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, SHADOW_BOLT, 2.5),
				beginCast(62.5, SHADOW_BOLT, 2.5),
				beginCast(65, SHADOW_BOLT, 2.5),
				beginCast(67.5, SHADOW_BOLT, 2.5),
				beginCast(70, SHADOW_BOLT, 2.5),
				beginCast(72.5, SHADOW_BOLT, 2.5),
				beginCast(75, SHADOW_BOLT, 2.5),
				beginCast(77.5, SHADOW_BOLT, 2.5),
				beginCast(80, SHADOW_BOLT, 2.5),
				beginCast(82.5, SHADOW_BOLT, 2.5),
				beginCast(85, SHADOW_BOLT, 2.5),
				beginCast(87.5, SHADOW_BOLT, 2.5),
				beginCast(90, SHADOW_BOLT, 2.5),
				beginCast(92.5, SHADOW_BOLT, 2.5),
				beginCast(95, SHADOW_BOLT, 2.5),
				beginCast(97.5, SHADOW_BOLT, 2.5),
				beginCast(100, SHADOW_BOLT, 2.5),
				beginCast(102.5, SHADOW_BOLT, 2.5),
				beginCast(105, SHADOW_BOLT, 2.5),
				beginCast(107.5, SHADOW_BOLT, 2.5),
				beginCast(110, SHADOW_BOLT, 2.5),
				beginCast(112.5, SHADOW_BOLT, 2.5),
				beginCast(115, SHADOW_BOLT, 2.5),
				beginCast(117.5, SHADOW_BOLT, 2.5),
				beginCast(120, SHADOWBURN)
		);
	}

	@Test
	void curse_plus_shadow_bolt() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/warlock/25002-2050300142301-52500051020001");

		setScript("warlock-curse-plus-shadow-bolt");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, CURSE_OF_DOOM),
				beginCast(61.5, SHADOW_BOLT, 2.5),
				beginCast(64, SHADOW_BOLT, 2.5),
				beginCast(66.5, SHADOW_BOLT, 2.5),
				beginCast(69, SHADOW_BOLT, 2.5),
				beginCast(71.5, SHADOW_BOLT, 2.5),
				beginCast(74, SHADOW_BOLT, 2.5),
				beginCast(76.5, SHADOW_BOLT, 2.5),
				beginCast(79, SHADOW_BOLT, 2.5),
				beginCast(81.5, SHADOW_BOLT, 2.5),
				beginCast(84, SHADOW_BOLT, 2.5),
				beginCast(86.5, SHADOW_BOLT, 2.5),
				beginCast(89, SHADOW_BOLT, 2.5),
				beginCast(91.5, SHADOW_BOLT, 2.5),
				beginCast(94, SHADOW_BOLT, 2.5),
				beginCast(96.5, SHADOW_BOLT, 2.5),
				beginCast(99, SHADOW_BOLT, 2.5),
				beginCast(101.5, SHADOW_BOLT, 2.5),
				beginCast(104, SHADOW_BOLT, 2.5),
				beginCast(106.5, SHADOW_BOLT, 2.5),
				beginCast(109, SHADOW_BOLT, 2.5),
				beginCast(111.5, SHADOW_BOLT, 2.5),
				beginCast(114, SHADOW_BOLT, 2.5),
				beginCast(116.5, SHADOW_BOLT, 2.5),
				beginCast(119, SHADOWBURN)
		);
	}

	@Test
	void curse_plus_shadow_bolt_elements_instead_of_doom() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/warlock/25002-2050300142301-52500051020001");
		enableAsset(player, CURSE_OF_THE_ELEMENTS);

		setScript("warlock-curse-plus-shadow-bolt");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(0, CURSE_OF_THE_ELEMENTS),
				beginCast(60, SHADOW_BOLT, 2.5),
				beginCast(62.5, SHADOW_BOLT, 2.5),
				beginCast(65, SHADOW_BOLT, 2.5),
				beginCast(67.5, SHADOW_BOLT, 2.5),
				beginCast(70, SHADOW_BOLT, 2.5),
				beginCast(72.5, SHADOW_BOLT, 2.5),
				beginCast(75, SHADOW_BOLT, 2.5),
				beginCast(77.5, SHADOW_BOLT, 2.5),
				beginCast(80, SHADOW_BOLT, 2.5),
				beginCast(82.5, SHADOW_BOLT, 2.5),
				beginCast(85, SHADOW_BOLT, 2.5),
				beginCast(87.5, SHADOW_BOLT, 2.5),
				beginCast(90, SHADOW_BOLT, 2.5),
				beginCast(92.5, SHADOW_BOLT, 2.5),
				beginCast(95, SHADOW_BOLT, 2.5),
				beginCast(97.5, SHADOW_BOLT, 2.5),
				beginCast(100, SHADOW_BOLT, 2.5),
				beginCast(102.5, SHADOW_BOLT, 2.5),
				beginCast(105, SHADOW_BOLT, 2.5),
				beginCast(107.5, SHADOW_BOLT, 2.5),
				beginCast(110, SHADOW_BOLT, 2.5),
				beginCast(112.5, SHADOW_BOLT, 2.5),
				beginCast(115, SHADOW_BOLT, 2.5),
				beginCast(117.5, SHADOW_BOLT, 2.5),
				beginCast(120, SHADOWBURN)
		);
	}

	@Test
	void destro_shadow() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/warlock/25002-2050300142301-52500051020001");

		setScript("warlock-destro-shadow");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, CURSE_OF_DOOM),
				beginCast(61.5, CORRUPTION),
				beginCast(63, IMMOLATE, 1.5),
				beginCast(64.5, SHADOW_BOLT, 2.5),
				beginCast(67, SHADOW_BOLT, 2.5),
				beginCast(69.5, SHADOW_BOLT, 2.5),
				beginCast(72, SHADOW_BOLT, 2.5),
				beginCast(74.5, SHADOW_BOLT, 2.5),
				beginCast(77, SHADOW_BOLT, 2.5),
				beginCast(79.5, CORRUPTION),
				beginCast(81, IMMOLATE, 1.5),
				beginCast(82.5, SHADOW_BOLT, 2.5),
				beginCast(85, SHADOW_BOLT, 2.5),
				beginCast(87.5, SHADOW_BOLT, 2.5),
				beginCast(90, SHADOW_BOLT, 2.5),
				beginCast(92.5, SHADOW_BOLT, 2.5),
				beginCast(95, SHADOW_BOLT, 2.5),
				beginCast(97.5, CORRUPTION),
				beginCast(99, IMMOLATE, 1.5),
				beginCast(100.5, SHADOW_BOLT, 2.5),
				beginCast(103, SHADOW_BOLT, 2.5),
				beginCast(105.5, SHADOW_BOLT, 2.5),
				beginCast(108, SHADOW_BOLT, 2.5),
				beginCast(110.5, SHADOW_BOLT, 2.5),
				beginCast(113, SHADOW_BOLT, 2.5),
				beginCast(115.5, SHADOW_BOLT, 2.5),
				beginCast(118, SHOOT, 1.5),
				beginCast(119.5, SHADOWBURN)
		);
	}

	@Test
	void affliction() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/warlock/5500200512201115--50502051020001");

		setScript("warlock-affliction");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, AMPLIFY_CURSE),
				beginCast(60, CURSE_OF_DOOM),
				beginCast(61.5, CORRUPTION),
				beginCast(63, IMMOLATE, 1.5),
				beginCast(64.5, SIPHON_LIFE),
				beginCast(66, SHADOW_BOLT, 2.5),
				beginCast(68.5, SHADOW_BOLT, 2.5),
				beginCast(71, SHADOW_BOLT, 2.5),
				beginCast(73.5, SHADOW_BOLT, 2.5),
				beginCast(76, SHADOW_BOLT, 2.5),
				beginCast(78.5, IMMOLATE, 1.5),
				beginCast(80, CORRUPTION),
				beginCast(81.5, SHADOW_BOLT, 2.5),
				beginCast(84, SHADOW_BOLT, 2.5),
				beginCast(86.5, SHADOW_BOLT, 2.5),
				beginCast(89, SHADOW_BOLT, 2.5),
				beginCast(91.5, SHADOW_BOLT, 2.5),
				beginCast(94, IMMOLATE, 1.5),
				beginCast(95.5, SHADOW_BOLT, 2.5),
				beginCast(98, CORRUPTION),
				beginCast(99.5, SHADOW_BOLT, 2.5),
				beginCast(102, SHADOW_BOLT, 2.5),
				beginCast(104.5, SHADOW_BOLT, 2.5),
				beginCast(107, SHADOW_BOLT, 2.5),
				beginCast(109.5, SHADOW_BOLT, 2.5),
				beginCast(112, SHADOW_BOLT, 2.5),
				beginCast(114.5, SHADOW_BOLT, 2.5),
				beginCast(117, SHADOW_BOLT, 2.5),
				beginCast(119.5, SHADOWBURN)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Doomfinger");
	}
}
