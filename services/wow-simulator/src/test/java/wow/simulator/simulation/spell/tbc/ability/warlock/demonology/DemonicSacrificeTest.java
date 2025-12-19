package wow.simulator.simulation.spell.tbc.ability.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.ABILITY;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.EffectNames.BURNING_WISH;
import static wow.test.commons.EffectNames.TOUCH_OF_SHADOW;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class DemonicSacrificeTest extends TbcWarlockSpellSimulationTest {
	/*
	Summons an Imp under the command of the Warlock.
	 */

	@Test
	void sacrificing_imp() {
		player.cast(SUMMON_IMP);
		player.cast(DEMONIC_SACRIFICE);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, SUMMON_IMP, 10)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(10)
						.endCast(player, SUMMON_IMP)
						.decreasedResource(2742, MANA, player, SUMMON_IMP)
						.beginCast(player, DEMONIC_SACRIFICE)
						.endCast(player, DEMONIC_SACRIFICE)
						.effectApplied(BURNING_WISH, ABILITY, player, 1800),
				at(1810)
						.effectExpired(BURNING_WISH, ABILITY, player)
		);
	}

	@Test
	void sacrificing_succubus() {
		player.cast(SUMMON_SUCCUBUS);
		player.cast(DEMONIC_SACRIFICE);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, SUMMON_SUCCUBUS, 10)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(10)
						.endCast(player, SUMMON_SUCCUBUS)
						.decreasedResource(3428, MANA, player, SUMMON_SUCCUBUS)
						.beginCast(player, DEMONIC_SACRIFICE)
						.endCast(player, DEMONIC_SACRIFICE)
						.effectApplied(TOUCH_OF_SHADOW, ABILITY, player, 1800),
				at(1810)
						.effectExpired(TOUCH_OF_SHADOW, ABILITY, player)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(DEMONIC_SACRIFICE, 1);
	}
}
