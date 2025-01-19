package wow.simulator.simulation.spell.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.DIVINE_SPIRIT;
import static wow.commons.model.spell.ResourceType.MANA;

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
					.endCast(player, DIVINE_SPIRIT)
					.decreasedResource(680, MANA, player, DIVINE_SPIRIT)
					.effectApplied(DIVINE_SPIRIT, player, 30 * 60)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(30 * 60)
					.effectExpired(DIVINE_SPIRIT, player)
		);
	}

	@Test
	void spiritIsIncreased() {
		var spiritBefore = player.getStats().getSpirit();

		player.cast(DIVINE_SPIRIT);

		updateUntil(30);

		var spiritAfter = player.getStats().getSpirit();

		assertThat(spiritAfter).isEqualTo(spiritBefore + 50);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.DIVINE_SPIRIT, 1);
	}
}
