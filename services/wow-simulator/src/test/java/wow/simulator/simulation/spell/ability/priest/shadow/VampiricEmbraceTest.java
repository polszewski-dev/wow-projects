package wow.simulator.simulation.spell.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.CalcUtils.getPercentOf;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class VampiricEmbraceTest extends PriestSpellSimulationTest {
	/*
	Afflicts your target with Shadow energy that causes all party members to be healed for 15% of any Shadow spell damage you deal for 1 min.
	 */

	@Test
	void success() {
		player.cast(VAMPIRIC_EMBRACE);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, VAMPIRIC_EMBRACE)
						.beginGcd(player)
						.endCast(player, VAMPIRIC_EMBRACE)
						.decreasedResource(89, MANA, player, VAMPIRIC_EMBRACE)
						.cooldownStarted(player, VAMPIRIC_EMBRACE, 10)
						.effectApplied(VAMPIRIC_EMBRACE, target, 60),
				at(1.5)
						.endGcd(player),
				at(10)
						.cooldownExpired(player, VAMPIRIC_EMBRACE),
				at(60)
						.effectExpired(VAMPIRIC_EMBRACE, target)
		);
	}

	@Test
	void mindBlastDamageIsConvertedToHealing() {
		player.cast(VAMPIRIC_EMBRACE);
		player.cast(MIND_BLAST);

		updateUntil(60);

		assertEvents(
				event -> event.isDamage() || event.isHealing(),
				at(3)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.increasedResource(109, HEALTH, player, VAMPIRIC_EMBRACE)
		);
	}

	@Test
	void shadowWordPainDamageIsConvertedToHealing() {
		player.cast(VAMPIRIC_EMBRACE);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(60);

		assertEvents(
				event -> event.isDamage() || event.isHealing(),
				at(4.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(30, HEALTH, player, VAMPIRIC_EMBRACE),
				at(7.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(30, HEALTH, player, VAMPIRIC_EMBRACE),
				at(10.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(30, HEALTH, player, VAMPIRIC_EMBRACE),
				at(13.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(30, HEALTH, player, VAMPIRIC_EMBRACE),
				at(16.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(30, HEALTH, player, VAMPIRIC_EMBRACE),
				at(19.5)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.increasedResource(30, HEALTH, player, VAMPIRIC_EMBRACE)
		);
	}

	@Test
	void healthGainedFromMindBlast() {
		player.cast(VAMPIRIC_EMBRACE);
		player.cast(MIND_BLAST);

		updateUntil(60);

		assertHealthGained(VAMPIRIC_EMBRACE, player, getPercentOf(15, MIND_BLAST_INFO.damage()));
	}

	@Test
	void healthGainedFromShadowWordPain() {
		player.cast(VAMPIRIC_EMBRACE);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(60);

		for (int tickNo = 0; tickNo < SHADOW_WORD_PAIN_INFO.numTicks(); ++tickNo) {
			assertHealthGained(tickNo, VAMPIRIC_EMBRACE, player, getPercentOf(15, SHADOW_WORD_PAIN_INFO.tickDamage()));
		}
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.VAMPIRIC_EMBRACE, 1);
	}
}
