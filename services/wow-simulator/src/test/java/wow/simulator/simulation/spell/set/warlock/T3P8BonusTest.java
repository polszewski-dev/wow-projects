package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.LIFE_TAP;
import static wow.test.commons.TalentNames.IMPROVED_LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class T3P8BonusTest extends WarlockSpellSimulationTest {
	/*
	Reduces health cost of your Life Tap by 12%.
	 */

	@Test
	void healthCostIsIncreased() {
		enableTalent(IMPROVED_LIFE_TAP, 2);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		assertThat(player.getCurrentMana()).isEqualTo(959);// (582 + 0.8*272) * 1.2

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.beginGcd(player)
						.endCast(player, LIFE_TAP)
						.decreasedResource(843, HEALTH, player, LIFE_TAP)
						.increasedResource(959, MANA, player, LIFE_TAP),
				at(1.5)
						.endGcd(player)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Plagueheart Belt");
		equip("Plagueheart Bindings");
		equip("Plagueheart Circlet");
		equip("Plagueheart Gloves");
		equip("Plagueheart Leggings");
		equip("Plagueheart Robe");
		equip("Plagueheart Sandals");
		equip("Plagueheart Shoulderpads");
	}
}
