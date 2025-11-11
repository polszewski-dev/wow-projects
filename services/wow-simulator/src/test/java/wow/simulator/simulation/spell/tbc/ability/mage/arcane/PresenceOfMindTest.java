package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.AbilityNames.PRESENCE_OF_MIND;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class PresenceOfMindTest extends TbcMageSpellSimulationTest {
	/*
	When activated, your next Mage spell with a casting time less than 10 sec becomes an instant cast spell.
	 */

	@Test
	void success() {
		player.cast(PRESENCE_OF_MIND);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, PRESENCE_OF_MIND)
						.endCast(player, PRESENCE_OF_MIND)
						.cooldownStarted(player, PRESENCE_OF_MIND, 180)
						.effectApplied(PRESENCE_OF_MIND, player, Duration.INFINITE),
				at(180)
						.cooldownExpired(player, PRESENCE_OF_MIND)
		);
	}

	@Test
	void effect_removed_after_spell_cast() {
		player.cast(PRESENCE_OF_MIND);
		player.cast(FROSTBOLT);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, PRESENCE_OF_MIND)
						.endCast(player, PRESENCE_OF_MIND)
						.cooldownStarted(player, PRESENCE_OF_MIND, 180)
						.effectApplied(PRESENCE_OF_MIND, player, Duration.INFINITE)
						.beginCast(player, FROSTBOLT)
						.beginGcd(player)
						.endCast(player, FROSTBOLT)
						.decreasedResource(345, MANA, player, FROSTBOLT)
						.effectRemoved(PRESENCE_OF_MIND, player)
						.decreasedResource(655, HEALTH, target, FROSTBOLT),
				at(1.5)
						.endGcd(player),
				at(180)
						.cooldownExpired(player, PRESENCE_OF_MIND)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.PRESENCE_OF_MIND, 1);
	}
}
