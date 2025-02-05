package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class AshtongueTalismanOfAcumenTest extends PriestSpellSimulationTest {
	/*
	Equip: Each time your Shadow Word: Pain deals damage, it has a 10% chance to grant you 220 spell damage for 10 sec 
	and each time your Renew heals, it has a 10% chance to grant you 220 healing for 5 sec. (Proc chance: 10%)
	 */
	@Test
	void procIsTriggered() {
		eventsOnlyOnFollowingRolls(0, 1, 2, 3, 4, 5);

		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_WORD_PAIN)
						.endCast(player, SHADOW_WORD_PAIN)
						.decreasedResource(575, MANA, player, SHADOW_WORD_PAIN)
						.effectApplied(SHADOW_WORD_PAIN, target, 18)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectApplied("Ashtongue Talisman of Acumen", player, 10),
				at(6)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player, 10),
				at(9)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player, 10),
				at(12)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player, 10),
				at(15)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player, 10),
				at(18)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectRemoved("Ashtongue Talisman of Acumen", player)
						.effectApplied("Ashtongue Talisman of Acumen", player, 10)
						.effectExpired(SHADOW_WORD_PAIN, target),
				at(28)
						.effectExpired("Ashtongue Talisman of Acumen", player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var dmgBefore = player.getStats().getSpellDamage();

		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_WORD_PAIN);

		updateUntil(10);

		var dmgAfter = player.getStats().getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 220);
	}

	@Override
	protected void afterSetUp() {
		equip("Ashtongue Talisman of Acumen", TRINKET_1);
	}
}
