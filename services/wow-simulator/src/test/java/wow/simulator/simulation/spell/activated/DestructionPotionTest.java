package wow.simulator.simulation.spell.activated;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.GroupCooldownId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.DESTRUCTION_POTION;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
class DestructionPotionTest extends WarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(DESTRUCTION_POTION.getName());

		player.cast(DESTRUCTION_POTION);

		simulation.updateUntil(Time.at(150));

		assertEvents(
				at(0)
						.beginCast(player, DESTRUCTION_POTION)
						.endCast(player, DESTRUCTION_POTION)
						.cooldownStarted(player, DESTRUCTION_POTION)
						.cooldownStarted(player, GroupCooldownId.POTION)
						.effectApplied(DESTRUCTION_POTION, player),
				at(15)
						.effectExpired(DESTRUCTION_POTION, player),
				at(120)
						.cooldownExpired(player, DESTRUCTION_POTION)
						.cooldownExpired(player, GroupCooldownId.POTION)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		player.getConsumables().enable(DESTRUCTION_POTION.getName());
		
		var dmgBefore = player.getStats().getSpellDamage();
		var critPctBefore = player.getStats().getSpellCritPct();

		player.cast(DESTRUCTION_POTION);

		simulation.updateUntil(Time.at(10));

		var dmgAfter = player.getStats().getSpellDamage();
		var critPctAfter = player.getStats().getSpellCritPct();

		assertThat(dmgAfter).isEqualTo(dmgBefore + 120);
		assertThat(critPctAfter).isEqualTo(critPctBefore + 2);
	}
}
