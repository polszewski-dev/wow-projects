package wow.simulator.simulation.script.tbc;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import java.util.List;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class WarlockScriptTest extends TbcWarlockSpellSimulationTest {
	@Test
	void shadow_bolt_spam() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025");

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
		enableTalents("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025");

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
		enableTalents("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025");
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
		enableTalents("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025");

		setScript("warlock-destro-shadow");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, CURSE_OF_DOOM),
				beginCast(61.5, CORRUPTION, 2),
				beginCast(63.5, IMMOLATE, 1.5),
				beginCast(65, SHADOW_BOLT, 2.5),
				beginCast(67.5, SHADOW_BOLT, 2.5),
				beginCast(70, SHADOW_BOLT, 2.5),
				beginCast(72.5, SHADOW_BOLT, 2.5),
				beginCast(75, SHADOW_BOLT, 2.5),
				beginCast(77.5, SHADOW_BOLT, 2.5),
				beginCast(80, CORRUPTION, 2),
				beginCast(82, IMMOLATE, 1.5),
				beginCast(83.5, SHADOW_BOLT, 2.5),
				beginCast(86, SHADOW_BOLT, 2.5),
				beginCast(88.5, SHADOW_BOLT, 2.5),
				beginCast(91, SHADOW_BOLT, 2.5),
				beginCast(93.5, SHADOW_BOLT, 2.5),
				beginCast(96, SHADOW_BOLT, 2.5),
				beginCast(98.5, CORRUPTION, 2),
				beginCast(100.5, IMMOLATE, 1.5),
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

	@Test
	void affliction() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/warlock/550220001023510551031--5050005122");

		setScript("warlock-affliction");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, AMPLIFY_CURSE),
				beginCast(60, CURSE_OF_DOOM),
				beginCast(61.5, UNSTABLE_AFFLICTION, 1.5),
				beginCast(63, CORRUPTION),
				beginCast(64.5, IMMOLATE, 1.5),
				beginCast(66, SIPHON_LIFE),
				beginCast(67.5, SHADOW_BOLT, 2.5),
				beginCast(70, SHADOW_BOLT, 2.5),
				beginCast(72.5, SHADOW_BOLT, 2.5),
				beginCast(75, SHADOW_BOLT, 2.5),
				beginCast(77.5, SHADOW_BOLT, 2.5),
				beginCast(80, UNSTABLE_AFFLICTION, 1.5),
				beginCast(81.5, CORRUPTION),
				beginCast(83, IMMOLATE, 1.5),
				beginCast(84.5, SHADOW_BOLT, 2.5),
				beginCast(87, SHADOW_BOLT, 2.5),
				beginCast(89.5, SHADOW_BOLT, 2.5),
				beginCast(92, SHADOW_BOLT, 2.5),
				beginCast(94.5, SHADOW_BOLT, 2.5),
				beginCast(97, SHADOW_BOLT, 2.5),
				beginCast(99.5, UNSTABLE_AFFLICTION, 1.5),
				beginCast(101, CORRUPTION),
				beginCast(102.5, IMMOLATE, 1.5),
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
	void affliction_without_ua() {
		enableTalents("https://www.wowhead.com/tbc/talent-calc/warlock/55022000102351055103--50500051220001");

		setScript("warlock-affliction-without-ua");

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

	@Test
	void soc_spam() {
		enableTalent(SHADOWBURN);

		setScript("warlock-soc-spam");

		executeScenario(40, List.of(target, target2, target3, target4, target5));

		assertBeginCastEvents(
				beginCast(60, SEED_OF_CORRUPTION, 2),
				beginCast(62, SEED_OF_CORRUPTION, 2),
				beginCast(64, SEED_OF_CORRUPTION, 2),
				beginCast(66, SEED_OF_CORRUPTION, 2),
				beginCast(68, SEED_OF_CORRUPTION, 2),
				beginCast(70, SHADOWBURN),
				beginCast(71.5, SEED_OF_CORRUPTION, 2),
				beginCast(73.5, SEED_OF_CORRUPTION, 2),
				beginCast(75.5, SEED_OF_CORRUPTION, 2),
				beginCast(77.5, SEED_OF_CORRUPTION, 2),
				beginCast(79.5, SEED_OF_CORRUPTION, 2),
				beginCast(81.5, SHADOW_BOLT, 3),
				beginCast(84.5, SEED_OF_CORRUPTION, 2),
				beginCast(86.5, SEED_OF_CORRUPTION, 2),
				beginCast(88.5, SEED_OF_CORRUPTION, 2),
				beginCast(90.5, SEED_OF_CORRUPTION, 2),
				beginCast(92.5, SEED_OF_CORRUPTION, 2),
				beginCast(94.5, SHADOWBURN),
				beginCast(96, SEED_OF_CORRUPTION, 2),
				beginCast(98, SHOOT, 1.5)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Wand of the Forgotten Star");
	}
}
