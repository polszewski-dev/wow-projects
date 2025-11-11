package wow.simulator.simulation.spell.tbc.ability.shaman.restoration;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MANA_TIDE_TOTEM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ManaTideTotemTest extends TbcShamanSpellSimulationTest {
	/*
	Summons a Mana Tide Totem with 5 health at the feet of the caster for 12 sec that restores 6% of total mana every 3 seconds to group members within 20 yards.
	 */

	@Test
	void success() {
		player.setCurrentMana(127);
		player.cast(MANA_TIDE_TOTEM);

		player2.setCurrentMana(0);
		player3.setCurrentMana(0);
		player4.setCurrentMana(0);

		updateUntil(120);

		assertEvents(
				at(0)
						.beginCast(player, MANA_TIDE_TOTEM)
						.beginGcd(player)
						.endCast(player, MANA_TIDE_TOTEM)
						.decreasedResource(127, MANA, player, MANA_TIDE_TOTEM)
						.cooldownStarted(player, MANA_TIDE_TOTEM, 300)
						.effectApplied(MANA_TIDE_TOTEM, player, 12),
				at(1.5)
						.endGcd(player),
				at(3)
						.increasedResource(255, MANA, player, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player2, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player3, MANA_TIDE_TOTEM)
						.increasedResource(608, MANA, player4, MANA_TIDE_TOTEM),
				at(6)
						.increasedResource(255, MANA, player, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player2, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player3, MANA_TIDE_TOTEM)
						.increasedResource(608, MANA, player4, MANA_TIDE_TOTEM),
				at(9)
						.increasedResource(255, MANA, player, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player2, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player3, MANA_TIDE_TOTEM)
						.increasedResource(608, MANA, player4, MANA_TIDE_TOTEM),
				at(12)
						.increasedResource(255, MANA, player, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player2, MANA_TIDE_TOTEM)
						.increasedResource(257, MANA, player3, MANA_TIDE_TOTEM)
						.increasedResource(608, MANA, player4, MANA_TIDE_TOTEM)
						.effectExpired(MANA_TIDE_TOTEM, player)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.MANA_TIDE_TOTEM, 1);

		getCharacterService().equipGearSet(player4, "Wowhead TBC P5 BiS");
		player4.onResourcesNeedRefresh();

		player.getParty().add(player2, player3, player4);
	}
}
