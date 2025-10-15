package wow.simulator.simulation.spell.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DIVINE_SPIRIT;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class DivineSpiritTest extends PriestSpellSimulationTest {
	/*
	Holy power infuses the target, increasing their Spirit by 50 for 30 min.
	 */

	@Test
	void success() {
		player.cast(DIVINE_SPIRIT);

		updateUntil(30 * 60);

		assertEvents(
			at(0)
					.beginCast(player, DIVINE_SPIRIT)
					.beginGcd(player)
					.endCast(player, DIVINE_SPIRIT)
					.decreasedResource(680, MANA, player, DIVINE_SPIRIT)
					.effectApplied(DIVINE_SPIRIT, player, 30 * 60),
			at(1.5)
					.endGcd(player),
			at(30 * 60)
					.effectExpired(DIVINE_SPIRIT, player)
		);
	}

	@Test
	void spiritIsIncreased() {
		player.cast(DIVINE_SPIRIT);

		updateUntil(30);

		var spiritBefore = statsAt(0).getSpirit();
		var spiritAfter = statsAt(1).getSpirit();

		assertThat(spiritAfter).isEqualTo(spiritBefore + 50);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DIVINE_SPIRIT, 1);
	}
}
