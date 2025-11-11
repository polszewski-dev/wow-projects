package wow.simulator.simulation.spell.tbc.ability.paladin.retrition;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.AVENGING_WRATH;
import static wow.test.commons.AbilityNames.HOLY_SHOCK;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class AvengingWrathTest extends TbcPaladinSpellSimulationTest {
	/*
	Increases all damage caused by 30% for 20 sec.
	Causes Forebearance, preventing the use of Divine Shield, Divine Protection, Blessing of Protection again for 1 min.
	 */

	@Test
	void success() {
		player.cast(AVENGING_WRATH);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, AVENGING_WRATH)
						.endCast(player, AVENGING_WRATH)
						.decreasedResource(315, MANA, player, AVENGING_WRATH)
						.cooldownStarted(player, AVENGING_WRATH, 180)
						.effectApplied(AVENGING_WRATH, player, 20),
				at(20)
						.effectExpired(AVENGING_WRATH, player),
				at(180)
						.cooldownExpired(player, AVENGING_WRATH)
		);
	}

	@Test
	void damage_is_increased() {
		player.cast(AVENGING_WRATH);
		player.cast(HOLY_SHOCK);

		updateUntil(30);

		assertDamageDone(HOLY_SHOCK_INFO, target, 0, 30);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.HOLY_SHOCK, 1);
	}
}
