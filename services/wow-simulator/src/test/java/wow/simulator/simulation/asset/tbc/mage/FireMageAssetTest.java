package wow.simulator.simulation.asset.tbc.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.asset.tbc.TbcAssetTest;

import static wow.commons.model.character.RaceId.TROLL;
import static wow.test.commons.AbilityNames.ARCANE_BRILLIANCE;
import static wow.test.commons.AbilityNames.SCORCH;
import static wow.test.commons.TalentNames.IMPROVED_SCORCH;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
class FireMageAssetTest extends TbcAssetTest {
	@Test
	void has_correct_talents() {
		assertHasTalent(IMPROVED_SCORCH);
	}

	@Test
	void casts_arcane_brilliance_during_preparation_phase() {
		executePreparationPhase();

		assertSpellCast(0.0, ARCANE_BRILLIANCE, player);
	}

	@Test
	void casts_scorch_5_times_during_warm_up_phase() {
		executePreparationPhase();
		executeWarmUpPhase();

		assertSpellCast(3.0, SCORCH, target);
		assertSpellCast(4.5, SCORCH, target);
		assertSpellCast(6.0, SCORCH, target);
		assertSpellCast(7.5, SCORCH, target);
		assertSpellCast(9.0, SCORCH, target);
	}

	@Test
	void after_preparation_phase_player_has_arcane_brilliance_buff() {
		executePreparationPhase();
		executeWarmUpPhase();

		assertHasEffect(player, ARCANE_BRILLIANCE);
	}

	@Test
	void after_warm_up_phase_target_has_5_stacks_of_fire_vulnerability() {
		executePreparationPhase();
		executeWarmUpPhase();

		assertHasEffect(target, "Fire Vulnerability", 5);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();

		setAssetParams("Fire Mage", TROLL);
	}
}
