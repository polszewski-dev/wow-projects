package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.test.commons.AbilityNames.LIFE_TAP;
import static wow.test.commons.AbilityNames.SUMMON_IMP;
import static wow.test.commons.TalentNames.MANA_FEED;

/**
 * User: POlszewski
 * Date: 2026-08-27
 */
class ManaFeedTest extends TbcWarlockTalentSimulationTest {
	/*
	When you gain mana from Drain Mana or Life Tap spells, your pet gains 100% of the mana you gain.
	 */
	@ParameterizedTest
	@CsvSource({
			"1, 33",
			"2, 66",
			"3, 100"
	})
	void life_tap_also_causes_pet_to_get_mana(int rank, int pct) {
		enableTalent(MANA_FEED, rank);

		player.cast(SUMMON_IMP);
		player.immediateAction(() -> {
			setMana(player, 0);
			setMana(pet, 0);
		});
		player.cast(LIFE_TAP);

		updateUntil(30);

		assertManaGained(LIFE_TAP, player, 582);
		assertManaGained("Copy Mana Gained as Pet Mana", pet, getPercentOf(pct, 582));
	}
}
