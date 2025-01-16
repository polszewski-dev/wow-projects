package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
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
class QuagmirransEyeTest extends WarlockSpellSimulationTest {
	/*
	Equip: Your harmful spells have a chance to increase your spell haste rating by 320 for 6 secs. (Proc chance: 10%, 45s cooldown)
	 */
	@Test
	void procIsTriggered() {
		rng.eventRoll = true;

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
						.decreasedResource(607, HEALTH, false, target, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId, 45)
						.effectApplied("Quagmirran's Eye", player, 6),
				at(9)
						.effectExpired("Quagmirran's Eye", player),
				at(48)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var hasteBefore = player.getStats().getSpellHasteRating();

		rng.eventRoll = true;
		player.cast(SHADOW_BOLT);
		updateUntil(5);

		var hasteAfter = player.getStats().getSpellHasteRating();

		assertThat(hasteAfter).isEqualTo(hasteBefore + 320);
	}

	CooldownId cooldownId = CooldownId.of(100127683);

	@Override
	protected void afterSetUp() {
		equip("Quagmirran's Eye", TRINKET_1);
	}
}
