package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

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

	@Test
	void instant_frostbolt_receives_bonus_from_expirinc_arcane_power() {
		enableTalent(TalentNames.ARCANE_POWER);

		player.cast(FROSTBOLT);

		player.cast(ARCANE_POWER);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);

		player.cast(PRESENCE_OF_MIND);
		player.cast(FROSTBOLT);

		player.cast(FROSTBOLT);

		updateUntil(30);

		//normal
		assertDamageDone(0, FROSTBOLT_INFO, target, player, 0, 0);

		// arcane power
		assertDamageDone(1, FROSTBOLT_INFO, target, player, 0, 30);
		assertDamageDone(2, FROSTBOLT_INFO, target, player, 0, 30);
		assertDamageDone(3, FROSTBOLT_INFO, target, player, 0, 30);
		assertDamageDone(4, FROSTBOLT_INFO, target, player, 0, 30);
		assertDamageDone(5, FROSTBOLT_INFO, target, player, 0, 30);

		// pom + arcane power
		assertDamageDone(6, FROSTBOLT_INFO, target, player, 0, 30);

		// normal
		assertDamageDone(7, FROSTBOLT_INFO, target, player, 0, 0);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.PRESENCE_OF_MIND);
	}
}
