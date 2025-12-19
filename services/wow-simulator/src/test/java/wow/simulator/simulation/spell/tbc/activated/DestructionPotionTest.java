package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.GroupCooldownId;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.DESTRUCTION_POTION;
import static wow.test.commons.EffectNames.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
class DestructionPotionTest extends TbcWarlockSpellSimulationTest {
	/*
	Use: Increases spell critical chance by 2% and spell damage by 120 for 15 sec. (2 Min Cooldown)
	 */

	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(DESTRUCTION_POTION);

		player.cast(DESTRUCTION_POTION);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, DESTRUCTION_POTION)
						.endCast(player, DESTRUCTION_POTION)
						.cooldownStarted(player, DESTRUCTION_POTION, 120)
						.cooldownStarted(player, GroupCooldownId.POTION, 120)
						.effectApplied(DESTRUCTION, player, 15),
				at(15)
						.effectExpired(DESTRUCTION, player),
				at(120)
						.cooldownExpired(player, DESTRUCTION_POTION)
						.cooldownExpired(player, GroupCooldownId.POTION)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		player.getConsumables().enable(DESTRUCTION_POTION);

		player.cast(DESTRUCTION_POTION);

		updateUntil(10);

		var dmgBefore = statsAt(0).getSpellDamage();
		var dmgAfter = statsAt(1).getSpellDamage();
		var critPctBefore = statsAt(0).getSpellCritPct();
		var critPctAfter = statsAt(1).getSpellCritPct();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 120);
		assertThat(critPctAfter).isEqualTo(critPctBefore + 2);
	}
}
