package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ITEM;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class ShiffarsNexusHornTest extends WarlockSpellSimulationTest {
	/*
	Equip: Chance on spell critical hit to increase your spell damage and healing by 225 for 10 secs. (Proc chance: 20%, 45s cooldown)
	 */
	@Test
	void procIsTriggered() {
		critsOnlyOnFollowingRolls(0);
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(863, HEALTH, true, target, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId, 45)
						.effectApplied("Shiffar's Nexus-Horn", ITEM, player, 10),
				at(13)
						.effectExpired("Shiffar's Nexus-Horn", ITEM, player),
				at(48)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var spBefore = player.getStats().getSpellPower();

		critsOnlyOnFollowingRolls(0);
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);
		updateUntil(10);

		var spAfter = player.getStats().getSpellPower();

		assertThat(spAfter).isEqualTo(spBefore + 225);
	}

	CooldownId cooldownId = CooldownId.of(100128418);

	@Override
	protected void afterSetUp() {
		equip("Shiffar's Nexus-Horn", TRINKET_1);
	}
}
