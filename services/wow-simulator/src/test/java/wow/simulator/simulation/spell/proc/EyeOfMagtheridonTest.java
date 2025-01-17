package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class EyeOfMagtheridonTest extends WarlockSpellSimulationTest {
	/*
	Equip: Grants 170 increased spell damage for 10 sec when one of your spells is resisted.
	 */
	@Test
	void procIsTriggered() {
		rng.hitRoll = false;
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
						.spellResisted(player, SHADOW_BOLT, target)
						.effectApplied("Eye of Magtheridon", player, 10),
				at(13)
						.effectExpired("Eye of Magtheridon", player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var dmgBefore = player.getStats().getSpellDamage();

		rng.hitRoll = false;
		rng.eventRoll = true;
		player.cast(SHADOW_BOLT);
		simulation.updateUntil(Time.at(10));

		var dmgAfter = player.getStats().getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 170);
	}

	@Override
	protected void afterSetUp() {
		equip("Eye of Magtheridon", TRINKET_1);
	}
}
