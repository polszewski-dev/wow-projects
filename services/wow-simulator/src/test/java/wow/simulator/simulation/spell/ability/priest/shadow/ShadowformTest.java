package wow.simulator.simulation.spell.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.spell.AbilityId.SHADOWFORM;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class ShadowformTest extends PriestSpellSimulationTest {
	/*
	Assume a Shadowform, increasing your Shadow damage by 15% and reducing Physical damage done to you by 15%.
	However, you may not cast Holy spells while in this form.
	 */

	@Test
	void success() {
		player.cast(SHADOWFORM);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOWFORM)
						.beginGcd(player)
						.endCast(player, SHADOWFORM)
						.decreasedResource(1435, MANA, player, SHADOWFORM)
						.cooldownStarted(player, SHADOWFORM, 1.5)
						.effectApplied(SHADOWFORM, player, Duration.INFINITE),
				at(1.5)
						.endGcd(player)
						.cooldownExpired(player, SHADOWFORM)
		);
	}

	@Test
	void shadowDamageIsIncreased() {
		player.cast(SHADOWFORM);
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertDamageDone(MIND_BLAST, MIND_BLAST_INFO.damage(), 15);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.SHADOWFORM, 1);
	}
}
