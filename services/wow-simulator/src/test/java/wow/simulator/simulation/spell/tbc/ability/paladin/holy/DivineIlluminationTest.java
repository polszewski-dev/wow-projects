package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.CONSECRATION;
import static wow.test.commons.AbilityNames.DIVINE_ILLUMINATION;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class DivineIlluminationTest extends TbcPaladinSpellSimulationTest {
	/*
	Reduces the mana cost of all spells by 50% for 15 sec.
	 */

	@Test
	void success() {
		player.cast(DIVINE_ILLUMINATION);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, DIVINE_ILLUMINATION)
						.endCast(player, DIVINE_ILLUMINATION)
						.cooldownStarted(player, DIVINE_ILLUMINATION, 180)
						.effectApplied(DIVINE_ILLUMINATION, player, 15),
				at(15)
						.effectExpired(DIVINE_ILLUMINATION, player),
				at(180)
						.cooldownExpired(player, DIVINE_ILLUMINATION)
		);
	}

	@Test
	void mana_cost_reduced() {
		player.cast(DIVINE_ILLUMINATION);
		player.cast(CONSECRATION);

		updateUntil(30);

		assertManaPaid(CONSECRATION, player, CONSECRATION_INFO.manaCost(), -50);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DIVINE_ILLUMINATION, 1);
	}
}
