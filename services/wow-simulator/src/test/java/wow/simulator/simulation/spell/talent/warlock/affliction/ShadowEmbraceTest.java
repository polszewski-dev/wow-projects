package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;
import static wow.commons.model.talent.TalentId.SHADOW_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShadowEmbraceTest extends WarlockSpellSimulationTest {
	/*
	Your Corruption, Curse of Agony, Siphon Life and Seed of Corruption spells also cause the Shadow Embrace effect, which reduces physical damage caused by 5%.
	 */

	@Test
	void effectIsTriggeredByCurseOfAgony() {
		enableTalent(SHADOW_EMBRACE, 5);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertEvents(
				EventCollectingHandler.Event::isEffect,
				at(0)
						.effectApplied(CURSE_OF_AGONY, target, 24)
						.effectApplied(SHADOW_EMBRACE, target, Duration.INFINITE),
				at(24)
						.effectExpired(CURSE_OF_AGONY, target)
						.effectRemoved(SHADOW_EMBRACE, target)
		);
	}

	@Test
	void effectIsTriggeredByCorruption() {
		enableTalent(SHADOW_EMBRACE, 5);
		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				EventCollectingHandler.Event::isEffect,
				at(0)
						.effectApplied(CORRUPTION, target, 18)
						.effectApplied(SHADOW_EMBRACE, target, Duration.INFINITE),
				at(18)
						.effectExpired(CORRUPTION, target)
						.effectRemoved(SHADOW_EMBRACE, target)
		);
	}

	@Test
	void effectIsTriggeredBySiphonLife() {
		enableTalent(SHADOW_EMBRACE, 5);
		enableTalent(TalentId.SIPHON_LIFE, 1);

		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertEvents(
				EventCollectingHandler.Event::isEffect,
				at(0)
						.effectApplied(SIPHON_LIFE, target, 30)
						.effectApplied(SHADOW_EMBRACE, target, Duration.INFINITE),
				at(30)
						.effectExpired(SIPHON_LIFE, target)
						.effectRemoved(SHADOW_EMBRACE, target)
		);
	}

	@Test
	void shadowEmbraceIsEndedAfterLastSpellExpires() {
		enableTalent(SHADOW_EMBRACE, 5);
		enableTalent(IMPROVED_CORRUPTION, 5);
		enableTalent(TalentId.SIPHON_LIFE, 1);

		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);
		player.cast(SIPHON_LIFE);

		updateUntil(60);

		assertEvents(
				EventCollectingHandler.Event::isEffect,
				at(0)
						.effectApplied(CURSE_OF_AGONY, target, 24)
						.effectApplied(SHADOW_EMBRACE, target, Duration.INFINITE),
				at(1.5)
						.effectApplied(CORRUPTION, target, 18)
						.effectChargesIncreased(SHADOW_EMBRACE, target, 2),
				at(3)
						.effectApplied(SIPHON_LIFE, target, 30)
						.effectChargesIncreased(SHADOW_EMBRACE, target, 3),
				at(19.5)
						.effectExpired(CORRUPTION, target)
						.effectChargesDecreased(SHADOW_EMBRACE, target, 2),
				at(24)
						.effectExpired(CURSE_OF_AGONY, target)
						.effectChargesDecreased(SHADOW_EMBRACE, target, 1),
				at(33)
						.effectExpired(SIPHON_LIFE, target)
						.effectRemoved(SHADOW_EMBRACE, target)
		);
	}
}
