package wow.simulator.simulation.spell.tbc.ability.shaman.restoration;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;
import static wow.test.commons.AbilityNames.NATURES_SWIFTNESS;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class NaturesSwiftnessTest extends TbcShamanSpellSimulationTest {
	/*
	When activated, your next Nature spell with a casting time less than 10 sec. becomes an instant cast spell.
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
		player.cast(LIGHTNING_BOLT);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, NATURES_SWIFTNESS)
						.endCast(player, NATURES_SWIFTNESS)
						.cooldownStarted(player, NATURES_SWIFTNESS, 180)
						.effectApplied(NATURES_SWIFTNESS, player, Duration.INFINITE)
						.beginCast(player, LIGHTNING_BOLT)
						.beginGcd(player)
						.endCast(player, LIGHTNING_BOLT)
						.decreasedResource(300, MANA, player, LIGHTNING_BOLT)
						.effectRemoved(NATURES_SWIFTNESS, player)
						.decreasedResource(611, HEALTH, target, LIGHTNING_BOLT),
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
