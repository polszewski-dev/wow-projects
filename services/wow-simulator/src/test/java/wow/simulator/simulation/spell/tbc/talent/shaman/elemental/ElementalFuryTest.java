package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.ELEMENTAL_FURY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ElementalFuryTest extends TbcShamanTalentSimulationTest {
	/*
	Increases the critical strike damage bonus of your Searing, Magma, and Fire Nova Totems and your Fire, Frost, and Nature spells by 100%.
	 */

	@Test
	void frost_crit_damage_bonus_is_increased() {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(ELEMENTAL_FURY, 1, FROST_SHOCK);

		assertCritDamageBonusIsIncreasedByPct(100);
	}

	@Test
	void fire_crit_damage_bonus_is_increased() {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(ELEMENTAL_FURY, 1, FLAME_SHOCK);

		assertCritDamageBonusIsIncreasedByPct(100);
	}

	@Test
	void nature_crit_damage_bonus_is_increased() {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(ELEMENTAL_FURY, 1, EARTH_SHOCK);

		assertCritDamageBonusIsIncreasedByPct(100);
	}
}
