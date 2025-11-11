package wow.simulator.simulation.spell.tbc.ability.druid.restoration;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.NATURES_SWIFTNESS;
import static wow.test.commons.AbilityNames.WRATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class NaturesSwiftnessTest extends TbcDruidSpellSimulationTest {
	/*
	When activated, your next Nature spell becomes an instant cast spell.
	 */

	@Test
	void success() {
		player.cast(NATURES_SWIFTNESS);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, NATURES_SWIFTNESS)
						.endCast(player, NATURES_SWIFTNESS)
						.cooldownStarted(player, NATURES_SWIFTNESS, 180)
						.effectApplied(NATURES_SWIFTNESS, player, Duration.INFINITE),
				at(180)
						.cooldownExpired(player, NATURES_SWIFTNESS)
		);
	}

	@Test
	void effect_removed_after_spell_cast() {
		player.cast(NATURES_SWIFTNESS);
		player.cast(WRATH);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, NATURES_SWIFTNESS)
						.endCast(player, NATURES_SWIFTNESS)
						.cooldownStarted(player, NATURES_SWIFTNESS, 180)
						.effectApplied(NATURES_SWIFTNESS, player, Duration.INFINITE)
						.beginCast(player, WRATH)
						.beginGcd(player)
						.endCast(player, WRATH)
						.decreasedResource(255, MANA, player, WRATH)
						.effectRemoved(NATURES_SWIFTNESS, player)
						.decreasedResource(407, HEALTH, target, WRATH),
				at(1.5)
						.endGcd(player),
				at(180)
						.cooldownExpired(player, NATURES_SWIFTNESS)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.NATURES_SWIFTNESS, 1);
	}
}
