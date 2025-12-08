package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BLAST_WAVE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class BlastWaveTest extends TbcMageSpellSimulationTest {
	/*
	A wave of flame radiates outward from the caster, damaging all enemies caught within the blast for 616 to 724 Fire damage, and Dazing them for 6 sec.
	 */

	@Test
	void success() {
		player.cast(BLAST_WAVE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, BLAST_WAVE)
						.beginGcd(player)
						.endCast(player, BLAST_WAVE)
						.decreasedResource(620, MANA, player, BLAST_WAVE)
						.cooldownStarted(player, BLAST_WAVE, 30)
						.decreasedResource(670, HEALTH, target, BLAST_WAVE)
						.decreasedResource(670, HEALTH, target2, BLAST_WAVE)
						.decreasedResource(670, HEALTH, target3, BLAST_WAVE)
						.decreasedResource(670, HEALTH, target4, BLAST_WAVE),
				at(1.5)
						.endGcd(player),
				at(30)
						.cooldownExpired(player, BLAST_WAVE)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(BLAST_WAVE, spellDamage);

		assertDamageDone(BLAST_WAVE_INFO, target, spellDamage);
		assertDamageDone(BLAST_WAVE_INFO, target2, spellDamage);
		assertDamageDone(BLAST_WAVE_INFO, target3, spellDamage);
		assertDamageDone(BLAST_WAVE_INFO, target4, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.BLAST_WAVE, 1);
	}
}
