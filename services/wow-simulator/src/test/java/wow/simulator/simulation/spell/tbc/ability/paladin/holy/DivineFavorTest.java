package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class DivineFavorTest extends TbcPaladinSpellSimulationTest {
	/*
	When activated, gives your next Flash of Light, Holy Light, or Holy Shock spell a 100% critical effect chance.
	 */

	@Test
	void success() {
		player.cast(DIVINE_FAVOR);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, DIVINE_FAVOR)
						.endCast(player, DIVINE_FAVOR)
						.decreasedResource(118, MANA, player, DIVINE_FAVOR)
						.cooldownStarted(player, DIVINE_FAVOR, 120)
						.effectApplied(DIVINE_FAVOR, player, Duration.INFINITE),
				at(120)
						.cooldownExpired(player, DIVINE_FAVOR)
		);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			FLASH_OF_LIGHT,
			HOLY_LIGHT,
			HOLY_SHOCK
	})
	void crit_chance_increased(String abilityName) {
		player.cast(DIVINE_FAVOR);
		player.cast(abilityName);

		updateUntil(30);

		assertLastCritChance(100);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DIVINE_FAVOR, 1);
		enableTalent(TalentNames.HOLY_SHOCK, 1);
	}
}
