package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.Duration;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.MISERY;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class MiseryTest extends PriestSpellSimulationTest {
	/*
	Your Shadow Word: Pain, Mind Flay and Vampiric Touch spells also cause the target to take an additional 5% spell damage.
	 */

	@Test
	void effectIsTriggeredByShadowWordPain() {
		enableTalent(MISERY, 5);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_WORD_PAIN)
						.beginGcd(player)
						.endCast(player, SHADOW_WORD_PAIN)
						.decreasedResource(575, MANA, player, SHADOW_WORD_PAIN)
						.effectApplied(MISERY, target, Duration.INFINITE)
						.effectApplied(SHADOW_WORD_PAIN, target, 18),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(216, HEALTH, target, SHADOW_WORD_PAIN),
				at(6)
						.decreasedResource(216, HEALTH, target, SHADOW_WORD_PAIN),
				at(9)
						.decreasedResource(216, HEALTH, target, SHADOW_WORD_PAIN),
				at(12)
						.decreasedResource(217, HEALTH, target, SHADOW_WORD_PAIN),
				at(15)
						.decreasedResource(216, HEALTH, target, SHADOW_WORD_PAIN),
				at(18)
						.decreasedResource(216, HEALTH, target, SHADOW_WORD_PAIN)
						.effectExpired(SHADOW_WORD_PAIN, target)
						.effectRemoved(MISERY, target)
		);
	}

	@Test
	void effectIsTriggeredByMindFlay() {
		enableTalent(MISERY, 5);
		enableTalent(TalentId.MIND_FLAY, 1);

		player.cast(MIND_FLAY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MIND_FLAY, 3)
						.beginGcd(player)
						.endCast(player, MIND_FLAY)
						.decreasedResource(230, MANA, player, MIND_FLAY)
						.effectApplied(MISERY, target, Duration.INFINITE)
						.beginChannel(player, MIND_FLAY)
						.effectApplied(MIND_FLAY, target, 3),
				at(1)
						.decreasedResource(184, HEALTH, target, MIND_FLAY),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(185, HEALTH, target, MIND_FLAY),
				at(3)
						.decreasedResource(185, HEALTH, target, MIND_FLAY)
						.effectExpired(MIND_FLAY, target)
						.endChannel(player, MIND_FLAY)
						.effectRemoved(MISERY, target)
		);
	}

	@Test
	void effectIsTriggeredByVampiricTouch() {
		enableTalent(MISERY, 5);
		enableTalent(TalentId.VAMPIRIC_TOUCH, 1);

		player.cast(VAMPIRIC_TOUCH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, VAMPIRIC_TOUCH, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, VAMPIRIC_TOUCH)
						.decreasedResource(425, MANA, player, VAMPIRIC_TOUCH)
						.effectApplied(MISERY, target, Duration.INFINITE)
						.effectApplied(VAMPIRIC_TOUCH, target, 15)
						.endGcd(player),
				at(4.5)
						.decreasedResource(136, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
				at(7.5)
						.decreasedResource(137, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
				at(10.5)
						.decreasedResource(136, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
				at(13.5)
						.decreasedResource(137, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH),
				at(16.5)
						.decreasedResource(136, HEALTH, target, VAMPIRIC_TOUCH)
						.increasedResource(6, MANA, player, VAMPIRIC_TOUCH)
						.effectExpired(VAMPIRIC_TOUCH, target)
						.effectRemoved(MISERY, target)
		);
	}

	@Test
	void miseryIsEndedAfterLastSpellExpires() {
		enableTalent(MISERY, 5);
		enableTalent(TalentId.MIND_FLAY, 1);

		player.cast(SHADOW_WORD_PAIN);
		player.cast(MIND_FLAY);

		updateUntil(30);

		assertEvents(
				TestEvent::isEffect,
				at(0)
						.effectApplied(MISERY, target, Duration.INFINITE)
						.effectApplied(SHADOW_WORD_PAIN, target, 18),
				at(1.5)
						.effectChargesIncreased(MISERY, target, 2)
						.effectApplied(MIND_FLAY, target, 3),
				at(4.5)
						.effectExpired(MIND_FLAY, target)
						.effectChargesDecreased(MISERY, target, 1),
				at(18)
						.effectExpired(SHADOW_WORD_PAIN, target)
						.effectRemoved(MISERY, target)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spellDamageIsIncreased(int rank) {
		enableTalent(MISERY, rank);
		enableTalent(TalentId.MIND_FLAY, 1);

		player.cast(SHADOW_WORD_PAIN);
		player.cast(MIND_FLAY);

		updateUntil(30);

		assertDamageDone(MIND_FLAY, MIND_FLAY_INFO.damage(), rank);
	}
}
