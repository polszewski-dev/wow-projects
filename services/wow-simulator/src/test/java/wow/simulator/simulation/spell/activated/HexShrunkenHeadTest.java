package wow.simulator.simulation.spell.activated;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.GroupCooldownId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.categorization.ItemSlot.TRINKET_2;
import static wow.commons.model.spell.AbilityId.HEX_SHRUNKEN_HEAD;

/**
 * User: POlszewski
 * Date: 2024-11-17
 */
class HexShrunkenHeadTest extends SpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		equip("Hex Shrunken Head", TRINKET_1);

		player.cast(TRINKET_1);

		simulation.updateUntil(Time.at(150));

		assertEvents(
				at(0)
						.beginCast(player, HEX_SHRUNKEN_HEAD)
						.endCast(player, HEX_SHRUNKEN_HEAD)
						.cooldownStarted(player, HEX_SHRUNKEN_HEAD)
						.cooldownStarted(player, GroupCooldownId.TRINKET)
						.effectApplied(HEX_SHRUNKEN_HEAD, player),
				at(20)
						.cooldownExpired(player, GroupCooldownId.TRINKET)
						.effectExpired(HEX_SHRUNKEN_HEAD, player),
				at(120)
						.cooldownExpired(player, HEX_SHRUNKEN_HEAD)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		equip("Hex Shrunken Head", TRINKET_1);

		var dmgBefore = player.getStats().getSpellPower();

		player.cast(TRINKET_1);

		simulation.updateUntil(Time.at(10));

		var dmgAfter = player.getStats().getSpellPower();

		assertThat(dmgBefore).isEqualTo(53);
		assertThat(dmgAfter).isEqualTo(53 + 211);
	}

	@Test
	void canNotUseSecondTrinketWhileFirstIsActive() {
		equip("Hex Shrunken Head", TRINKET_1);
		equip("Shifting Naaru Sliver", TRINKET_2);

		player.cast(TRINKET_1);

		simulation.updateUntil(Time.at(10));

		assertThat(player.canCast(TRINKET_1)).isFalse();
		assertThat(player.canCast(TRINKET_2)).isFalse();
	}

	@Test
	void canUseSecondTrinketWhileFirstIsNotActive() {
		equip("Hex Shrunken Head", TRINKET_1);
		equip("Shifting Naaru Sliver", TRINKET_2);

		player.cast(TRINKET_1);

		simulation.updateUntil(Time.at(20));

		assertThat(player.canCast(TRINKET_1)).isFalse();
		assertThat(player.canCast(TRINKET_2)).isTrue();
	}
}
