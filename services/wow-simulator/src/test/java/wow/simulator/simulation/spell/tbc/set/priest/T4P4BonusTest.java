package wow.simulator.simulation.spell.tbc.set.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.MIND_FLAY;
import static wow.test.commons.AbilityNames.SMITE;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T4P4BonusTest extends TbcPriestSpellSimulationTest {
	/*
	Your Mind Flay and Smite spells deal 5% more damage.
	*/

	@Test
	void mindFLayDamageIsIncreased() {
		enableTalent(TalentNames.MIND_FLAY, 1);

		player.cast(MIND_FLAY);

		updateUntil(30);

		assertDamageDone(MIND_FLAY, MIND_FLAY_INFO.damage(totalSpellDamage), 5);
	}

	@Test
	void smiteDamageIsIncreased() {
		player.cast(SMITE);

		updateUntil(30);

		assertDamageDone(SMITE, SMITE_INFO.damage(totalSpellDamage), 5);
	}

	@Override
	protected void afterSetUp() {
		equip("Gloves of the Incarnate");
		equip("Leggings of the Incarnate");
		equip("Shroud of the Incarnate");
		equip("Soul-Collar of the Incarnate");
	}
}