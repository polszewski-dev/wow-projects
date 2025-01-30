package wow.simulator.simulation.spell.set.priest;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_FLAY;
import static wow.commons.model.spell.AbilityId.SMITE;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T4P4BonusTest extends PriestSpellSimulationTest {
	/*
	Your Mind Flay and Smite spells deal 5% more damage.
	*/

	@Test
	void mindFLayDamageIsIncreased() {
		enableTalent(TalentId.MIND_FLAY, 1);

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