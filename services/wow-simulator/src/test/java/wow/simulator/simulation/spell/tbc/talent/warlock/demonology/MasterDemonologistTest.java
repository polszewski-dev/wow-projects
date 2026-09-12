package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.LASH_OF_PAIN_INFO;
import static wow.simulator.simulation.spell.tbc.TbcSpellInfos.SHADOW_BOLT_INFO;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.MASTER_DEMONOLOGIST;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class MasterDemonologistTest extends TbcWarlockTalentSimulationTest {
	/*
	Grants both the Warlock and the summoned demon an effect as long as that demon is active.
	Imp - Reduces threat caused by 20%.
	Voidwalker - Reduces physical damage taken by 10%.
	Succubus/Incubus - Increases all damage caused by 10%.
	Felhunter - Increases all resistances by 1 per level.
	Felguard - Increases all damage caused by 5% and all resistances by .5 per level.
	 */
	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void player_receives_the_buff_while_pet_is_active(int rank) {
		enableTalent(MASTER_DEMONOLOGIST, rank);

		player.cast(SUMMON_SUCCUBUS);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertDamageDone(SHADOW_BOLT_INFO, target, 0, 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void pet_receives_the_buff(int rank) {
		enableTalent(MASTER_DEMONOLOGIST, rank);

		player.cast(SUMMON_SUCCUBUS);
		player.petCast(LASH_OF_PAIN);

		updateUntil(30);

		assertDamageDone(LASH_OF_PAIN_INFO, target, pet, 0, 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void player_without_active_pet_does_not_receive_the_buff(int rank) {
		enableTalent(MASTER_DEMONOLOGIST, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertDamageDone(SHADOW_BOLT_INFO, target, 0, 0);
	}
}
