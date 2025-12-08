package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.character.CreatureType;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HOLY_WRATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HolyWrathTest extends TbcPaladinSpellSimulationTest {
	/*
	Sends bolts of holy power in all directions, causing 637 to 748 Holy damage to all Undead and Demon targets within 20 yds.
	 */

	@Test
	void success() {
		player.cast(HOLY_WRATH);

		updateUntil(120);

		assertEvents(
				at(0)
						.beginCast(player, HOLY_WRATH, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, HOLY_WRATH)
						.decreasedResource(825, MANA, player, HOLY_WRATH)
						.cooldownStarted(player, HOLY_WRATH, 60)
						.decreasedResource(692, HEALTH, target, HOLY_WRATH)
						.decreasedResource(692, HEALTH, target2, HOLY_WRATH)
						.decreasedResource(692, HEALTH, target3, HOLY_WRATH)
						.decreasedResource(692, HEALTH, target4, HOLY_WRATH),
				at(62)
						.cooldownExpired(player, HOLY_WRATH)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(HOLY_WRATH, spellDamage);

		assertDamageDone(HOLY_WRATH_INFO, target, spellDamage);
		assertDamageDone(HOLY_WRATH_INFO, target2, spellDamage);
		assertDamageDone(HOLY_WRATH_INFO, target3, spellDamage);
		assertDamageDone(HOLY_WRATH_INFO, target4, spellDamage);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		enemyType = CreatureType.UNDEAD;
	}
}
