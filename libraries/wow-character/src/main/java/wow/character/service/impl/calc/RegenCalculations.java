package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedRegenStats;
import wow.character.model.snapshot.BaseStatsSnapshot;
import wow.character.model.snapshot.RegenSnapshot;
import wow.character.util.AttributeConditionArgs;

import static java.lang.Math.*;
import static wow.character.service.impl.calc.BaseStatsCalculations.getBaseStatsSnapshot;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class RegenCalculations extends Calculations {
	public static RegenSnapshot getRegenSnapshot(Character character) {
		var baseStats = getBaseStatsSnapshot(character);
		var regenStats = getRegenStats(character);

		var spiritBasedRegen = getSpiritBasedRegen(baseStats, character);
		var manaRegenPct = regenStats.getManaRegenPct();
		var mp5 = regenStats.getMp5();
		var inCombatManaRegenPct = clamp(regenStats.getInCombatManaRegenPct(), 0, 100);

		var uninterruptedManaRegen = (spiritBasedRegen + mp5) * (1 + manaRegenPct / 100);
		var interruptedManaRegen = (spiritBasedRegen * inCombatManaRegenPct / 100 + mp5) * (1 + manaRegenPct / 100);

		var baseHealthRegen = 0;
		var healthRegenPct = regenStats.getHealthRegenPct();
		var hp5 = regenStats.getHp5();
		var inCombatHealthRegenPct = clamp(regenStats.getInCombatHealthRegenPct(), 0, 100);

		var outOfCombatHealthRegen = (baseHealthRegen + hp5) * (1 + healthRegenPct / 100);
		var inCombatHealthRegen = (baseHealthRegen * inCombatHealthRegenPct / 100 + hp5) * (1 + healthRegenPct / 100);

		var snapshot = new RegenSnapshot();

		snapshot.setUninterruptedManaRegen((int) uninterruptedManaRegen);
		snapshot.setInterruptedManaRegen((int) interruptedManaRegen);
		snapshot.setOutOfCombatHealthRegen((int) outOfCombatHealthRegen);
		snapshot.setInCombatHealthRegen((int) inCombatHealthRegen);
		snapshot.setHealthGeneratedPct(regenStats.getHealingTakenPct());

		return snapshot;
	}

	private static int getSpiritBasedRegen(BaseStatsSnapshot baseStats, Character character) {
		var intellect = baseStats.getIntellect();
		var spirit = baseStats.getSpirit();
		var baseRegen = getBaseManaRegen(character);

		return (int) ceil(5 * (0.001 + sqrt(intellect) * spirit * baseRegen));
	}

	private static double getBaseManaRegen(Character character) {
		int level = character.getLevel();
		int maxLevel = BASE_MANA_REGEN_PER_LEVEL.length - 1;

		if (level > maxLevel) {
			return BASE_MANA_REGEN_PER_LEVEL[maxLevel];
		}

		return BASE_MANA_REGEN_PER_LEVEL[level];
	}

	private static AccumulatedRegenStats getRegenStats(Character character) {
		var conditionArgs = AttributeConditionArgs.forRegen(character);
		var regenStats = new AccumulatedRegenStats(conditionArgs);

		accumulateEffects(character, regenStats);

		return regenStats;
	}

	private static final double[] BASE_MANA_REGEN_PER_LEVEL = {
			0,
			0.034965,
			0.034191,
			0.033465,
			0.032526,
			0.031661,
			0.031076,
			0.030523,
			0.029994,
			0.029307,
			0.028661,
			0.027584,
			0.026215,
			0.025381,
			0.024300,
			0.023345,
			0.022748,
			0.021958,
			0.021386,
			0.020790,
			0.020121,
			0.019733,
			0.019155,
			0.018819,
			0.018316,
			0.017936,
			0.017576,
			0.017201,
			0.016919,
			0.016581,
			0.016233,
			0.015994,
			0.015707,
			0.015464,
			0.015204,
			0.014956,
			0.014744,
			0.014495,
			0.014302,
			0.014094,
			0.013895,
			0.013724,
			0.013522,
			0.013363,
			0.013175,
			0.012996,
			0.012853,
			0.012687,
			0.012539,
			0.012384,
			0.012233,
			0.012113,
			0.011973,
			0.011859,
			0.011714,
			0.011575,
			0.011473,
			0.011342,
			0.011245,
			0.011110,
			0.010999,
			0.010700,
			0.010522,
			0.010290,
			0.010119,
			0.009968,
			0.009808,
			0.009651,
			0.009553,
			0.009445,
			0.009327,
	};
}
