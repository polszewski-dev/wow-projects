package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class AshtongueTalismanOfShadowsTest extends WarlockSpellSimulationTest {
	/*
	Equip: Each time your Corruption deals damage, it has a 20% chance to grant you 220 spell damage for 5 sec. (Proc chance: 20%)
	 */
	@Test
	void procIsTriggered() {
		rng.eventRoll = true;

		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectApplied("Ashtongue Talisman of Shadows", player, 5),
				at(6)
						.decreasedResource(184, HEALTH, target, CORRUPTION)
						.effectRemoved("Ashtongue Talisman of Shadows", player)
						.effectApplied("Ashtongue Talisman of Shadows", player, 5),
				at(9)
						.decreasedResource(184, HEALTH, target, CORRUPTION)
						.effectRemoved("Ashtongue Talisman of Shadows", player)
						.effectApplied("Ashtongue Talisman of Shadows", player, 5),
				at(12)
						.decreasedResource(184, HEALTH, target, CORRUPTION)
						.effectRemoved("Ashtongue Talisman of Shadows", player)
						.effectApplied("Ashtongue Talisman of Shadows", player, 5),
				at(15)
						.decreasedResource(185, HEALTH, target, CORRUPTION)
						.effectRemoved("Ashtongue Talisman of Shadows", player)
						.effectApplied("Ashtongue Talisman of Shadows", player, 5),
				at(18)
						.decreasedResource(184, HEALTH, target, CORRUPTION)
						.effectRemoved("Ashtongue Talisman of Shadows", player)
						.effectApplied("Ashtongue Talisman of Shadows", player, 5)
						.effectExpired(CORRUPTION, target),
				at(23)
						.effectExpired("Ashtongue Talisman of Shadows", player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var dmgBefore = player.getStats().getSpellDamage();

		rng.eventRoll = true;
		enableTalent(IMPROVED_CORRUPTION, 5);
		player.cast(CORRUPTION);
		updateUntil(10);

		var dmgAfter = player.getStats().getSpellDamage();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 220);
	}

	@Override
	protected void afterSetUp() {
		equip("Ashtongue Talisman of Shadows", TRINKET_1);
	}
}
