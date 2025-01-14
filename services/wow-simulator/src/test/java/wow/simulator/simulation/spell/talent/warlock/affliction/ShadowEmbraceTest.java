package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CURSE_OF_AGONY;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.SHADOW_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
@Disabled
class ShadowEmbraceTest extends WarlockSpellSimulationTest {
	/*
	Your Corruption, Curse of Agony, Siphon Life and Seed of Corruption spells also cause the Shadow Embrace effect, which reduces physical damage caused by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void curseOfAgonyTriggersEffect(int rank) {
		enableTalent(SHADOW_EMBRACE, rank);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY)
						.endCast(player, CURSE_OF_AGONY)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.effectApplied(CURSE_OF_AGONY, target, 24)
						.effectApplied(SHADOW_EMBRACE, target, 1)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(4)
						.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
				at(6)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(8)
						.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
				at(10)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(12)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(14)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(16)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(18)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(20)
						.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY),
				at(22)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(24)
						.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY)
						.effectExpired(CURSE_OF_AGONY, target)
						.effectRemoved(SHADOW_EMBRACE, target)
		);
	}
}
