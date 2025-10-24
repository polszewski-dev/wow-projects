package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.POWER_INFUSION;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class PowerInfusionTest extends TbcPriestSpellSimulationTest {
	/*
	Infuses the target with power, increasing spell casting speed by 20% and reducing the mana cost of all spells by 20%.  Lasts 15 sec.
	 */

	@Test
	void success() {
		player.cast(POWER_INFUSION);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, POWER_INFUSION)
						.endCast(player, POWER_INFUSION)
						.decreasedResource(717, MANA, player, POWER_INFUSION)
						.cooldownStarted(player, POWER_INFUSION, 180)
						.effectApplied(POWER_INFUSION, player, 15),
				at(15)
						.effectExpired(POWER_INFUSION, player),
				at(180)
						.cooldownExpired(player, POWER_INFUSION)
		);
	}

	@Test
	void effectIsNotRemovedAfterSpellCast() {
		player.cast(POWER_INFUSION);
		player.cast(MIND_BLAST);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, POWER_INFUSION)
						.endCast(player, POWER_INFUSION)
						.decreasedResource(717, MANA, player, POWER_INFUSION)
						.cooldownStarted(player, POWER_INFUSION, 180)
						.effectApplied(POWER_INFUSION, player, 15)
						.beginCast(player, MIND_BLAST, 1.25)
						.beginGcd(player),
				at(1.25)
						.endCast(player, MIND_BLAST)
						.decreasedResource(360, MANA, player, MIND_BLAST)
						.cooldownStarted(player, MIND_BLAST, 8)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.endGcd(player),
				at(9.25)
						.cooldownExpired(player, MIND_BLAST),
				at(15)
						.effectExpired(POWER_INFUSION, player),
				at(180)
						.cooldownExpired(player, POWER_INFUSION)
		);
	}

	@Test
	void castTimeIsReduced() {
		player.cast(POWER_INFUSION);
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertCastTime(MIND_BLAST, MIND_BLAST_INFO.baseCastTime() / (1 + 0.2));
	}

	@Test
	void manaCostIsReduced() {
		player.cast(POWER_INFUSION);
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertManaPaid(MIND_BLAST, player, MIND_BLAST_INFO.manaCost(), -20);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.POWER_INFUSION, 1);
	}
}
