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
class SextantOfUnstableCurrentsTest extends WarlockSpellSimulationTest {
	/*
	Equip: Your spell critical strikes have a chance to increase your spell damage and healing by 190 for 15 sec. (Proc chance: 20%, 45s cooldown)
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
						.effectApplied("Sextant of Unstable Currents", player),
				at(18)
						.effectExpired("Sextant of Unstable Currents", player),
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

		assertThat(spAfter).isEqualTo(spBefore + 190);
	}

	CooldownId cooldownId = CooldownId.of(100130626);

	@Override
	protected void afterSetUp() {
		equip("Sextant of Unstable Currents", TRINKET_1);
	}
}
