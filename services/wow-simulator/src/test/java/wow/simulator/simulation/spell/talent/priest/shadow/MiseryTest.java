package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_FLAY;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.MISERY;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
@Disabled
class MiseryTest extends PriestSpellSimulationTest {
	/*
	Your Shadow Word: Pain, Mind Flay and Vampiric Touch spells also cause the target to take an additional 5% spell damage.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void effectIsTriggered(int rank) {
		enableTalent(MISERY, rank);
		enableTalent(TalentId.MIND_FLAY, 1);

		player.cast(MIND_FLAY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MIND_FLAY, 3)
						.endCast(player, MIND_FLAY)
						.decreasedResource(230, MANA, player, MIND_FLAY)
						.beginChannel(player, MIND_FLAY)
						.effectApplied(MIND_FLAY, target, 3)
						.effectApplied(MISERY, target, 3)
						.beginGcd(player),
				at(1)
						.decreasedResource(177, HEALTH, target, MIND_FLAY),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(178, HEALTH, target, MIND_FLAY),
				at(3)
						.decreasedResource(178, HEALTH, target, MIND_FLAY)
						.effectExpired(MIND_FLAY, target)
						.effectExpired(MISERY, target)
						.endChannel(player, MIND_FLAY)
		);
	}
}
