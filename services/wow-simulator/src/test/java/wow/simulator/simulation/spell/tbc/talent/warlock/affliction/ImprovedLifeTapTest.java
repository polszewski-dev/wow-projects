package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcSpellInfos;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import java.util.List;
import java.util.stream.Stream;

import static wow.test.commons.AbilityNames.LIFE_TAP;
import static wow.test.commons.TalentNames.IMPROVED_LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedLifeTapTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the amount of Mana awarded by your Life Tap spell by 20%.
	 */

	@ParameterizedTest
	@MethodSource("getRankAndSpellPowerList")
	void increased_mana_awarded(RankAndSpellPower data) {
		var rank = data.rank;
		var spellPower = data.spellPower;

		addSpBonus(spellPower);
		enableTalent(IMPROVED_LIFE_TAP, rank);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		updateUntil(30);

		int actualAmount = getManaDifference(player);
		int amountWithoutTalent = (int) LIFE_TAP_INFO.damage(spellPower);

		assertIsIncreasedByPct(actualAmount, amountWithoutTalent, 10 * rank);

		assertDamageDone(LIFE_TAP, player, amountWithoutTalent, 10 * rank);
		assertManaGained(LIFE_TAP, player, amountWithoutTalent, 10 * rank);
	}

	record RankAndSpellPower(int rank, int spellPower) {}
	
	static List<RankAndSpellPower> getRankAndSpellPowerList() {
		return Stream.of(1, 2)
				.flatMap(rank -> TbcSpellInfos.spellPowerLevels().stream().map(sd -> new RankAndSpellPower(rank, sd)))
				.toList();
	} 
}
