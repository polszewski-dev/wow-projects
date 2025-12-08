package wow.simulator.simulation.spell.tbc.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOWBURN;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ShadowBurnTest extends TbcWarlockSpellSimulationTest {
	/*
	Instantly blasts the target for 597 to 665 Shadow damage.
	 If the target dies within 5 sec of Shadowburn, and yields experience or honor, the caster gains a Soul Shard.
	 */

	@Test
	void success() {
		player.cast(SHADOWBURN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN)
						.beginGcd(player)
						.endCast(player, SHADOWBURN)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.cooldownStarted(player, SHADOWBURN, 15)
						.decreasedResource(631, HEALTH, target, SHADOWBURN),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, SHADOWBURN)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SHADOWBURN, spellDamage);

		assertDamageDone(SHADOWBURN_INFO, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.SHADOWBURN, 1);
	}
}
