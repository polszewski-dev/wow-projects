package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.AbilityNames.TOTEM_OF_WRATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class TotemOfWrathTest extends TbcShamanSpellSimulationTest {
	/*
	Summons a Totem of Wrath with 5 health at the feet of the caster.
	The totem increases the chance to hit and critically strike with spells by 3% for all party members within 20 yards. Lasts 2 min.
	 */

	@Test
	void success() {
		player.cast(TOTEM_OF_WRATH);

		updateUntil(120);

		assertEvents(
				at(0)
						.beginCast(player, TOTEM_OF_WRATH)
						.beginGcd(player)
						.endCast(player, TOTEM_OF_WRATH)
						.decreasedResource(212, MANA, player, TOTEM_OF_WRATH)
						.effectApplied(TOTEM_OF_WRATH, player, 120),
				at(1.5)
						.endGcd(player),
				at(120)
						.effectExpired(TOTEM_OF_WRATH, player)
		);
	}

	@Test
	void hit_chance_increased() {
		player.idleFor(Duration.seconds(4));
		player.cast(TOTEM_OF_WRATH);

		player2.cast(SHADOW_BOLT);
		player2.cast(SHADOW_BOLT);

		updateUntil(30);

		var baseHitChance = rng.getHitRollData().getRollChances().getFirst();

		assertLastHitChance(baseHitChance + 3);
	}

	@Test
	void crit_chance_increased() {
		player.idleFor(Duration.seconds(4));
		player.cast(TOTEM_OF_WRATH);

		player2.cast(SHADOW_BOLT);
		player2.cast(SHADOW_BOLT);

		updateUntil(30);

		var baseRollChance = rng.getCritRollData().getRollChances().getFirst();

		assertLastCritChance(baseRollChance + 3);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.TOTEM_OF_WRATH, 1);

		player.getParty().add(player2);
	}
}
