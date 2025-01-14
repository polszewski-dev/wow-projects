package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.LIFE_TAP;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.IMPROVED_LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class WarlockT3P8BonusTest extends WarlockSpellSimulationTest {
	@Test
	void t3Bonus() {
		enableTalent(IMPROVED_LIFE_TAP, 2);
		setMana(player, 0);

		equip("Plagueheart Belt");
		equip("Plagueheart Bindings");
		equip("Plagueheart Circlet");
		equip("Plagueheart Gloves");
		equip("Plagueheart Leggings");
		equip("Plagueheart Robe");
		equip("Plagueheart Sandals");
		equip("Plagueheart Shoulderpads");

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(959);// (582 + 0.8*272) * 1.2

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP)
						.endCast(player, LIFE_TAP)
						.decreasedResource(843, HEALTH, player, LIFE_TAP)
						.increasedResource(959, MANA, player, LIFE_TAP)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}
}
