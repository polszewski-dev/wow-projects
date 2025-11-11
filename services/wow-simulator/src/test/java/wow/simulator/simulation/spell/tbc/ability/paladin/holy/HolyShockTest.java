package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HOLY_SHOCK;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HolyShockTest extends TbcPaladinSpellSimulationTest {
	/*
	Blasts the target with Holy energy, causing 721 to 779 Holy damage to an enemy, or 913 to 987 healing to an ally.
	 */

	@Test
	void success() {
		player.cast(HOLY_SHOCK);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HOLY_SHOCK)
						.beginGcd(player)
						.endCast(player, HOLY_SHOCK)
						.decreasedResource(650, MANA, player, HOLY_SHOCK)
						.cooldownStarted(player, HOLY_SHOCK, 15)
						.decreasedResource(750, HEALTH, target, HOLY_SHOCK)
						.increasedResource(750, HEALTH, target, HOLY_SHOCK),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, HOLY_SHOCK)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(HOLY_SHOCK, spellDamage);

		assertDamageDone(HOLY_SHOCK_INFO, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.HOLY_SHOCK, 1);
	}
}
