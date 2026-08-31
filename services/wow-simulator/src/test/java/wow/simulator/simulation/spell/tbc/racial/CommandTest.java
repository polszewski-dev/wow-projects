package wow.simulator.simulation.spell.tbc.racial;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;
import static wow.test.commons.AbilityNames.FIREBOLT;
import static wow.test.commons.AbilityNames.SUMMON_IMP;

/**
 * User: POlszewski
 * Date: 2026-08-30
 */
class CommandTest extends TbcWarlockSpellSimulationTest {
	/*
	Damage dealt by Hunter and Warlock pets increased by 5%.
	 */

	@Test
	void pets_damage_is_increased() {
		player.cast(SUMMON_IMP);
		summonedPetCasts(player, FIREBOLT);

		updateUntil(30);

		assertDamageDone(FIREBOLT_INFO, target, pet, 0, 5);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		setPlayerConfig(WARLOCK, ORC);
	}
}
