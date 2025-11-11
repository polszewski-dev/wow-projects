package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ElementalMasteryTest extends TbcShamanSpellSimulationTest {
	/*
	When activated, this spell gives your next Fire, Frost, or Nature damage spell a 100% critical strike chance and reduces the mana cost by 100%.
	 */

	@Test
	void success() {
		player.cast(ELEMENTAL_MASTERY);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, ELEMENTAL_MASTERY)
						.endCast(player, ELEMENTAL_MASTERY)
						.cooldownStarted(player, ELEMENTAL_MASTERY, 180)
						.effectApplied(ELEMENTAL_MASTERY, player, Duration.INFINITE),
				at(180)
						.cooldownExpired(player, ELEMENTAL_MASTERY)
		);
	}

	@Test
	void spell_crits_and_costs_no_mana_and_effect_is_removed() {
		player.cast(ELEMENTAL_MASTERY);
		player.cast(LIGHTNING_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ELEMENTAL_MASTERY)
						.endCast(player, ELEMENTAL_MASTERY)
						.cooldownStarted(player, ELEMENTAL_MASTERY, 180)
						.effectApplied(ELEMENTAL_MASTERY, player, Duration.INFINITE)
						.beginCast(player, LIGHTNING_BOLT, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, LIGHTNING_BOLT)
						.effectRemoved(ELEMENTAL_MASTERY, player)
						.decreasedResource(917, HEALTH, true, target, LIGHTNING_BOLT)
		);
	}

	@ParameterizedTest
	@ValueSource(strings = { EARTH_SHOCK, FLAME_SHOCK, FROST_SHOCK })
	void ability_crit_chance_was_100_pct(String abilityName) {
		player.cast(ELEMENTAL_MASTERY);
		player.cast(abilityName);

		updateUntil(30);

		assertLastCritChance(100);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.ELEMENTAL_MASTERY, 1);
	}
}
