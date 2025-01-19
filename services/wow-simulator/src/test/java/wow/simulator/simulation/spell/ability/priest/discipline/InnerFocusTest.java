package wow.simulator.simulation.spell.ability.priest.discipline;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.INNER_FOCUS;
import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.spell.ResourceType.HEALTH;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class InnerFocusTest extends PriestSpellSimulationTest {
	/*
	When activated, reduces the mana cost of your next spell by 100%
	and increases its critical effect chance by 25% if it is capable of a critical effect.
	 */

	@Test
	void success() {
		player.cast(INNER_FOCUS);

		updateUntil(180);

		assertEvents(
			at(0)
					.beginCast(player, INNER_FOCUS)
					.endCast(player, INNER_FOCUS)
					.cooldownStarted(player, INNER_FOCUS, 180)
					.effectApplied(INNER_FOCUS, player, 15),
			at(15)
					.effectExpired(INNER_FOCUS, player),
			at(180)
					.cooldownExpired(player, INNER_FOCUS)
		);
	}

	@Test
	void effectIsRemovedAfterSpellCast() {
		player.cast(INNER_FOCUS);
		player.cast(MIND_BLAST);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, INNER_FOCUS)
						.endCast(player, INNER_FOCUS)
						.cooldownStarted(player, INNER_FOCUS, 180)
						.effectApplied(INNER_FOCUS, player, 15)
						.beginCast(player, MIND_BLAST, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, MIND_BLAST)
						.cooldownStarted(player, MIND_BLAST, 8)
						.effectRemoved(INNER_FOCUS, player)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.endGcd(player),
				at(9.5)
						.cooldownExpired(player, MIND_BLAST),
				at(180)
						.cooldownExpired(player, INNER_FOCUS)
		);
	}

	@Disabled
	@Test
	void critChanceIncreased() {
		var critChanceBefore = player.getStats().getSpellCritPct();

		player.cast(INNER_FOCUS);
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertLastCritChance(critChanceBefore + 25);
	}

	@Test
	void manaCostIsZero() {
		player.cast(INNER_FOCUS);
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertManaPaid(MIND_BLAST, player, 0);
	}


	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.INNER_FOCUS, 1);
	}
}
