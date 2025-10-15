package wow.simulator.simulation.spell.activated;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.GroupCooldownId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.categorization.ItemSlot.TRINKET_2;
import static wow.test.commons.AbilityNames.HEX_SHRUNKEN_HEAD;
import static wow.test.commons.AbilityNames.SHIFTING_NAARU_SLIVER;

/**
 * User: POlszewski
 * Date: 2024-11-17
 */
class HexShrunkenHeadTest extends WarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		equip(HEX_SHRUNKEN_HEAD, TRINKET_1);

		player.cast(HEX_SHRUNKEN_HEAD);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, HEX_SHRUNKEN_HEAD)
						.endCast(player, HEX_SHRUNKEN_HEAD)
						.cooldownStarted(player, HEX_SHRUNKEN_HEAD, 120)
						.cooldownStarted(player, GroupCooldownId.TRINKET, 20)
						.effectApplied(HEX_SHRUNKEN_HEAD, player, 20),
				at(20)
						.cooldownExpired(player, GroupCooldownId.TRINKET)
						.effectExpired(HEX_SHRUNKEN_HEAD, player),
				at(120)
						.cooldownExpired(player, HEX_SHRUNKEN_HEAD)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		equip(HEX_SHRUNKEN_HEAD, TRINKET_1);

		player.cast(HEX_SHRUNKEN_HEAD);

		updateUntil(10);

		var dmgBefore = statsAt(0).getSpellPower();
		var dmgAfter = statsAt(1).getSpellPower();

		assertThat(dmgBefore).isEqualTo(53);
		assertThat(dmgAfter).isEqualTo(53 + 211);
	}

	@Test
	void canNotUseSecondTrinketWhileFirstIsActive() {
		equip(HEX_SHRUNKEN_HEAD, TRINKET_1);
		equip(SHIFTING_NAARU_SLIVER, TRINKET_2);

		player.cast(HEX_SHRUNKEN_HEAD);

		updateUntil(10);

		assertThat(player.canCast(HEX_SHRUNKEN_HEAD)).isFalse();
		assertThat(player.canCast(SHIFTING_NAARU_SLIVER)).isFalse();
	}

	@Test
	void canUseSecondTrinketWhileFirstIsNotActive() {
		equip(HEX_SHRUNKEN_HEAD, TRINKET_1);
		equip(SHIFTING_NAARU_SLIVER, TRINKET_2);

		player.cast(HEX_SHRUNKEN_HEAD);

		updateUntil(20);

		assertThat(player.canCast(HEX_SHRUNKEN_HEAD)).isFalse();
		assertThat(player.canCast(SHIFTING_NAARU_SLIVER)).isTrue();
	}
}
