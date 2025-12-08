package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ARCANE_BLAST;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ArcaneBlastTest extends TbcMageSpellSimulationTest {
	/*
	Blasts the target with energy, dealing 668 to 772 Arcane damage.
	Each time you cast Arcane Blast, the casting time is reduced while mana cost is increased. Effect stacks up to 3 times and lasts 8 sec.
	 */

	@Test
	void success() {
		player.cast(ARCANE_BLAST);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_BLAST, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, ARCANE_BLAST)
						.decreasedResource(195, MANA, player, ARCANE_BLAST)
						.decreasedResource(720, HEALTH, target, ARCANE_BLAST)
						.effectApplied(ARCANE_BLAST, player, 8),
				at(10.5)
						.effectExpired(ARCANE_BLAST, player)
		);
	}

	@Test
	void subsequent_casts_modify_cast_time_and_mana_cost() {
		player.cast(ARCANE_BLAST);
		player.cast(ARCANE_BLAST);
		player.cast(ARCANE_BLAST);
		player.cast(ARCANE_BLAST);
		player.cast(ARCANE_BLAST);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_BLAST, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, ARCANE_BLAST)
						.decreasedResource(195, MANA, player, ARCANE_BLAST)
						.decreasedResource(720, HEALTH, target, ARCANE_BLAST)
						.effectApplied(ARCANE_BLAST, player, 8)
						.beginCast(player, ARCANE_BLAST, Duration.millis(2_167))
						.beginGcd(player),
				at(4)
						.endGcd(player),
				atMillis(4_667)
						.endCast(player, ARCANE_BLAST)
						.decreasedResource(341, MANA, player, ARCANE_BLAST)
						.decreasedResource(720, HEALTH, target, ARCANE_BLAST)
						.effectStacked(ARCANE_BLAST, player, 2)
						.beginCast(player, ARCANE_BLAST, Duration.millis(1_833))
						.beginGcd(player),
				atMillis(6_167)
						.endGcd(player),
				at(6.5)
						.endCast(player, ARCANE_BLAST)
						.decreasedResource(487, MANA, player, ARCANE_BLAST)
						.decreasedResource(720, HEALTH, target, ARCANE_BLAST)
						.effectStacked(ARCANE_BLAST, player, 3)
						.beginCast(player, ARCANE_BLAST, 1.5)
						.beginGcd(player),
				at(8)
						.endCast(player, ARCANE_BLAST)
						.decreasedResource(633, MANA, player, ARCANE_BLAST)
						.decreasedResource(720, HEALTH, target, ARCANE_BLAST)
						.effectStacked(ARCANE_BLAST, player, 3)
						.endGcd(player)
						.beginCast(player, ARCANE_BLAST, 1.5)
						.beginGcd(player),
				at(9.5)
						.endCast(player, ARCANE_BLAST)
						.decreasedResource(633, MANA, player, ARCANE_BLAST)
						.decreasedResource(720, HEALTH, target, ARCANE_BLAST)
						.effectStacked(ARCANE_BLAST, player, 3)
						.endGcd(player),
				at(17.5)
						.effectExpired(ARCANE_BLAST, player)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(ARCANE_BLAST, spellDamage);

		assertDamageDone(ARCANE_BLAST_INFO, spellDamage);
	}
}
