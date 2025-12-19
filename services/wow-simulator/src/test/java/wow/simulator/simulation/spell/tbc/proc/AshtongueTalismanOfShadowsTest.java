package wow.simulator.simulation.spell.tbc.proc;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.EffectNames.POWER_OF_THE_ASHTONGUE;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class AshtongueTalismanOfShadowsTest extends TbcWarlockSpellSimulationTest {
	/*
	Equip: Each time your Corruption deals damage, it has a 20% chance to grant you 220 spell damage for 5 sec. (Proc chance: 20%)
	 */
	@Test
	void procIsTriggered() {
		eventsOnlyOnFollowingRolls(0, 1, 2, 3, 4, 5);

		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.beginGcd(player)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectApplied(POWER_OF_THE_ASHTONGUE, ITEM, player, 5),
				at(6)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectRemoved(POWER_OF_THE_ASHTONGUE, ITEM, player)
						.effectApplied(POWER_OF_THE_ASHTONGUE, ITEM, player, 5),
				at(9)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectRemoved(POWER_OF_THE_ASHTONGUE, ITEM, player)
						.effectApplied(POWER_OF_THE_ASHTONGUE, ITEM, player, 5),
				at(12)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectRemoved(POWER_OF_THE_ASHTONGUE, ITEM, player)
						.effectApplied(POWER_OF_THE_ASHTONGUE, ITEM, player, 5),
				at(15)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectRemoved(POWER_OF_THE_ASHTONGUE, ITEM, player)
						.effectApplied(POWER_OF_THE_ASHTONGUE, ITEM, player, 5),
				at(18)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectRemoved(POWER_OF_THE_ASHTONGUE, ITEM, player)
						.effectApplied(POWER_OF_THE_ASHTONGUE, ITEM, player, 5)
						.effectExpired(CORRUPTION, target),
				at(23)
						.effectExpired(POWER_OF_THE_ASHTONGUE, ITEM, player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		eventsOnlyOnFollowingRolls(0, 1, 2);
		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		updateUntil(10);

		var dmgBefore = statsAt(0).getSpellDamage();
		var dmgAfter = statsAt(10).getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 220);
	}

	@Override
	protected void afterSetUp() {
		equip("Ashtongue Talisman of Shadows", TRINKET_1);
	}
}
