package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.SURGE_OF_LIGHT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SurgeOfLightTest extends TbcPriestTalentSimulationTest {
	/*
	Your spell criticals have a 50% chance to cause your next Smite spell to be instant cast, cost no mana but be incapable of a critical hit. This effect lasts 10 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void effectIsTriggered(int rank) {
		enableTalent(SURGE_OF_LIGHT, rank);

		critsOnlyOnFollowingRolls(0);
		eventsOnlyOnFollowingRolls(0);

		player.cast(SMITE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SMITE, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, SMITE)
						.decreasedResource(385, MANA, player, SMITE)
						.decreasedResource(873, HEALTH, true, target, SMITE)
						.effectApplied(SURGE_OF_LIGHT, TALENT, player, 10),
				at(12.5)
						.effectExpired(SURGE_OF_LIGHT, TALENT, player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void secondSmiteIsInstantCastAndConsumesTheEffect(int rank) {
		enableTalent(SURGE_OF_LIGHT, rank);

		critsOnlyOnFollowingRolls(0);
		eventsOnlyOnFollowingRolls(0);

		player.cast(SMITE);
		player.cast(SMITE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SMITE, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, SMITE)
						.decreasedResource(385, MANA, player, SMITE)
						.decreasedResource(873, HEALTH, true, target, SMITE)
						.effectApplied(SURGE_OF_LIGHT, TALENT, player, 10)
						.beginCast(player, SMITE)
						.beginGcd(player)
						.endCast(player, SMITE)
						.effectRemoved(SURGE_OF_LIGHT, TALENT, player)
						.decreasedResource(582, HEALTH, target, SMITE),
				at(4)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void correctEventChance(int rank) {
		enableTalent(SURGE_OF_LIGHT, rank);

		critsOnlyOnFollowingRolls(0);
		eventsOnlyOnFollowingRolls(0);

		player.cast(SMITE);

		updateUntil(30);

		assertLastEventChance(25 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void secondSmiteHasZeroCritChance(int rank) {
		enableTalent(SURGE_OF_LIGHT, rank);

		critsOnlyOnFollowingRolls(0);
		eventsOnlyOnFollowingRolls(0);

		player.cast(SMITE);
		player.cast(SMITE);

		updateUntil(30);

		assertLastCritChance(0);
	}
}
