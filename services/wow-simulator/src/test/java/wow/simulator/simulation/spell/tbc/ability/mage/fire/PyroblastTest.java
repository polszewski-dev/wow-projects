package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.PYROBLAST;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class PyroblastTest extends TbcMageSpellSimulationTest {
	/*
	Hurls an immense fiery boulder that causes 939 to 1191 Fire damage and an additional 356 Fire damage over 12 sec.
	 */

	@Test
	void success() {
		player.cast(PYROBLAST);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, PYROBLAST, 6)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(6)
						.endCast(player, PYROBLAST)
						.decreasedResource(500, MANA, player, PYROBLAST)
						.decreasedResource(1065, HEALTH, false, target, PYROBLAST)
						.effectApplied(PYROBLAST, target, 12),
				at(9)
						.decreasedResource(89, HEALTH, target, PYROBLAST),
				at(12)
						.decreasedResource(89, HEALTH, target, PYROBLAST),
				at(15)
						.decreasedResource(89, HEALTH, target, PYROBLAST),
				at(18)
						.decreasedResource(89, HEALTH, target, PYROBLAST)
						.effectExpired(PYROBLAST, target)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(PYROBLAST, spellDamage);

		assertDamageDone(PYROBLAST_INFO, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.PYROBLAST, 1);
	}
}
