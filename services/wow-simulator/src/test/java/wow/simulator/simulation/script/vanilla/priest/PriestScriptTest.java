package wow.simulator.simulation.script.vanilla.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.vanilla.VanillaPriestSpellSimulationTest;
import wow.test.commons.AssetNames;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-10-23
 */
class PriestScriptTest extends VanillaPriestSpellSimulationTest {
	@Test
	void shadow() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/priest/50520013--5032504103501051");
		enableAsset(AssetNames.SHADOWFORM);

		setScript("priest-shadow");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(0, SHADOWFORM),
				beginCast(60, SHADOW_WORD_PAIN),
				beginCast(61.5, DEVOURING_PLAGUE),
				beginCast(63, INNER_FOCUS),
				beginCast(63, MIND_BLAST, 1.5),
				beginCast(64.5, MIND_FLAY),
				beginCast(67.5, MIND_FLAY),
				beginCast(70.5, MIND_BLAST, 1.5),
				beginCast(72, MIND_FLAY),
				beginCast(75, MIND_FLAY),
				beginCast(78, MIND_BLAST, 1.5),
				beginCast(79.5, MIND_FLAY),
				beginCast(82.5, MIND_FLAY),
				beginCast(85.5, SHADOW_WORD_PAIN),
				beginCast(87, MIND_BLAST, 1.5),
				beginCast(88.5, MIND_FLAY),
				beginCast(91.5, MIND_FLAY),
				beginCast(94.5, MIND_BLAST, 1.5),
				beginCast(96, MIND_FLAY),
				beginCast(99, MIND_FLAY),
				beginCast(102, MIND_BLAST, 1.5),
				beginCast(103.5, MIND_FLAY),
				beginCast(106.5, MIND_FLAY),
				beginCast(109.5, MIND_BLAST, 1.5),
				beginCast(111, MIND_FLAY),
				beginCast(114, MIND_FLAY),
				beginCast(117, MIND_BLAST, 1.5),
				beginCast(118.5, SHOOT, 1.5)
		);
	}

	@Test
	void holy() {
		enableTalents("https://www.wowhead.com/classic/talent-calc/priest/50520013--5032504103501051");

		setScript("priest-holy");

		executeScenario(60);

		assertBeginCastEvents(
				beginCast(60, SHADOW_WORD_PAIN),
				beginCast(61.5, DEVOURING_PLAGUE),
				beginCast(63, HOLY_FIRE, 3.5),
				beginCast(66.5, INNER_FOCUS),
				beginCast(66.5, MIND_BLAST, 1.5),
				beginCast(68, SMITE, 2.5),
				beginCast(70.5, SMITE, 2.5),
				beginCast(73, HOLY_FIRE, 3.5),
				beginCast(76.5, MIND_BLAST, 1.5),
				beginCast(78, SMITE, 2.5),
				beginCast(80.5, SMITE, 2.5),
				beginCast(83, HOLY_FIRE, 3.5),
				beginCast(86.5, SHADOW_WORD_PAIN),
				beginCast(88, MIND_BLAST, 1.5),
				beginCast(89.5, SMITE, 2.5),
				beginCast(92, SMITE, 2.5),
				beginCast(94.5, HOLY_FIRE, 3.5),
				beginCast(98, MIND_BLAST, 1.5),
				beginCast(99.5, SMITE, 2.5),
				beginCast(102, SMITE, 2.5),
				beginCast(104.5, HOLY_FIRE, 3.5),
				beginCast(108, MIND_BLAST, 1.5),
				beginCast(109.5, SMITE, 2.5),
				beginCast(112, SMITE, 2.5),
				beginCast(114.5, SMITE, 2.5),
				beginCast(117, MIND_BLAST, 1.5),
				beginCast(118.5, SHOOT, 1.5)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Doomfinger");
	}
}
