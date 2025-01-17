package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

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
		rng.critRoll = true;
		rng.eventRoll = true;

		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(60));

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
						.effectApplied("Shiffar's Nexus-Horn", player),
				at(13)
						.effectExpired("Shiffar's Nexus-Horn", player),
				at(48)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var spBefore = player.getStats().getSpellPower();

		rng.critRoll = true;
		rng.eventRoll = true;
		player.cast(SHADOW_BOLT);
		simulation.updateUntil(Time.at(10));

		var spAfter = player.getStats().getSpellPower();

		assertThat(spAfter).isEqualTo(spBefore + 225);
	}

	CooldownId cooldownId = CooldownId.of(100128418);

	@Override
	protected void afterSetUp() {
		equip("Shiffar's Nexus-Horn", TRINKET_1);
	}
}
