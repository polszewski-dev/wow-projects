package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;
import wow.test.commons.TalentNames;

import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;
import static wow.test.commons.TalentNames.SHADOW_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShadowEmbraceTest extends TbcWarlockSpellSimulationTest {
	/*
	Your Corruption, Curse of Agony, Siphon Life and Seed of Corruption spells also cause the Shadow Embrace effect, which reduces physical damage caused by 5%.
	 */

	@Test
	void effectIsTriggeredByCurseOfAgony() {
		enableTalent(SHADOW_EMBRACE, 5);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(SHADOW_EMBRACE, TALENT, target, Duration.INFINITE)
						.effectApplied(CURSE_OF_AGONY, target, 24),
				at(24)
						.effectExpired(CURSE_OF_AGONY, target)
						.effectRemoved(SHADOW_EMBRACE, TALENT, target)
		);
	}

	@Test
	void effectIsTriggeredByCorruption() {
		enableTalent(SHADOW_EMBRACE, 5);
		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(SHADOW_EMBRACE, TALENT, target, Duration.INFINITE)
						.effectApplied(CORRUPTION, target, 18),
				at(18)
						.effectExpired(CORRUPTION, target)
						.effectRemoved(SHADOW_EMBRACE, TALENT, target)
		);
	}

	@Test
	void effectIsTriggeredBySiphonLife() {
		enableTalent(SHADOW_EMBRACE, 5);
		enableTalent(TalentNames.SIPHON_LIFE, 1);

		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(SHADOW_EMBRACE, TALENT, target, Duration.INFINITE)
						.effectApplied(SIPHON_LIFE, target, 30),
				at(30)
						.effectExpired(SIPHON_LIFE, target)
						.effectRemoved(SHADOW_EMBRACE, TALENT, target)
		);
	}

	@Test
	void shadowEmbraceIsEndedAfterLastSpellExpires() {
		enableTalent(SHADOW_EMBRACE, 5);
		enableTalent(IMPROVED_CORRUPTION, 5);
		enableTalent(TalentNames.SIPHON_LIFE, 1);

		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);
		player.cast(SIPHON_LIFE);

		updateUntil(60);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(SHADOW_EMBRACE, TALENT, target, Duration.INFINITE)
						.effectApplied(CURSE_OF_AGONY, target, 24),
				at(1.5)
						.effectChargesIncreased(SHADOW_EMBRACE, TALENT, target, 2)
						.effectApplied(CORRUPTION, target, 18),
				at(3)
						.effectChargesIncreased(SHADOW_EMBRACE, TALENT, target, 3)
						.effectApplied(SIPHON_LIFE, target, 30),
				at(19.5)
						.effectExpired(CORRUPTION, target)
						.effectChargesDecreased(SHADOW_EMBRACE, TALENT, target, 2),
				at(24)
						.effectExpired(CURSE_OF_AGONY, target)
						.effectChargesDecreased(SHADOW_EMBRACE, TALENT, target, 1),
				at(33)
						.effectExpired(SIPHON_LIFE, target)
						.effectRemoved(SHADOW_EMBRACE, TALENT, target)
		);
	}
}
