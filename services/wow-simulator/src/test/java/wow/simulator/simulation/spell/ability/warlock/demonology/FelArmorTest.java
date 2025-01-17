package wow.simulator.simulation.spell.ability.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.FEL_ARMOR;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class FelArmorTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
		player.cast(FEL_ARMOR);

		simulation.updateUntil(Time.at(30 * 60));

		assertEvents(
				at(0)
						.beginCast(player, FEL_ARMOR)
						.endCast(player, FEL_ARMOR)
						.decreasedResource(725, MANA, player, FEL_ARMOR)
						.effectApplied(FEL_ARMOR, player, 30 * 60)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(30 * 60)
						.effectExpired(FEL_ARMOR, player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		var dmgBefore = player.getStats().getSpellDamage();

		player.cast(FEL_ARMOR);
		simulation.updateUntil(Time.at(30));

		var dmgAfter = player.getStats().getSpellDamage();

		assertThat(dmgBefore).isZero();
		assertThat(dmgAfter).isEqualTo(100);
	}
}
