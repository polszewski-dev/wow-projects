package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.character.CreatureType;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.EXORCISM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ExorcismTest extends TbcPaladinSpellSimulationTest {
	/*
	Causes 626 to 698 Holy damage to an Undead or Demon target.
	 */

	@Test
	void success() {
		player.cast(EXORCISM);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, EXORCISM)
						.beginGcd(player)
						.endCast(player, EXORCISM)
						.decreasedResource(340, MANA, player, EXORCISM)
						.cooldownStarted(player, EXORCISM, 15)
						.decreasedResource(662, HEALTH, target, EXORCISM),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, EXORCISM)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(EXORCISM, spellDamage);

		assertDamageDone(EXORCISM_INFO, spellDamage);
	}

	@ParameterizedTest
	@CsvSource({
			"UNDEAD, true",
			"DEMON, true",
			"BEAST, false",
	})
	void can_only_be_casted_on_certain_creature_types(CreatureType creatureType, boolean expected) {
		var target = getEnemy("Target", creatureType);

		var actual = player.canCast(EXORCISM, target);

		assertThat(actual).isEqualTo(expected);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		enemyType = CreatureType.UNDEAD;
	}
}
