package wow.commons.util;

import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2020-09-18
 */
public final class SpellCalculations {
	public static final Boolean ALWAYS_CRIT = true;
	public static final Boolean NEVER_CRIT = false;
	public static final Boolean AVERAGE_CRIT = null;

	public static SpellStatistics getDamage(int baseDmgMin, int baseDmgMax, int baseDmgDoT, Snapshot snapshot, Boolean hasCrit) {
		double hitChance = snapshot.hitChance;
		double critChance = snapshot.critChance;
		double critCoeff = snapshot.critCoeff;
		double sp = snapshot.sp;
		double spMultiplier = snapshot.spMultiplier;
		double coeffDirect = snapshot.spellCoeffDirect;
		double coeffDoT = snapshot.spellCoeffDoT;
		double directDamageDoneMultiplier = snapshot.directDamageDoneMultiplier;
		double dotDamageDoneMultiplier = snapshot.dotDamageDoneMultiplier;

		Duration castTime = snapshot.effectiveCastTime;

		if (hasCrit != null) {
			critChance = hasCrit ? 1 : 0;
		}

		// direct damage

		double directDamage = 0;

		if (baseDmgMin + baseDmgMax != 0) {
			directDamage += (baseDmgMin + baseDmgMax) / 2.0;
			directDamage += coeffDirect * sp * spMultiplier;
			directDamage *= directDamageDoneMultiplier;
			directDamage *= hitChance;
			directDamage *= (1 - critChance) * 1 + critChance * critCoeff;
		}

		// dot damage

		double dotDamage = 0;

		if (baseDmgDoT != 0) {
			dotDamage = baseDmgDoT;
			dotDamage += coeffDoT * sp * spMultiplier;
			dotDamage *= dotDamageDoneMultiplier;
			dotDamage *= hitChance;
		}

		SpellStatistics result = new SpellStatistics();

		result.snapshot = snapshot;
		result.totalDamage = directDamage + dotDamage;
		result.castTime = castTime;
		result.dps = result.totalDamage / result.castTime.getSeconds();
		result.manaCost = snapshot.manaCost;
		result.dpm = result.totalDamage / result.manaCost;

		return result;
	}

	public static int getScaledValue(int base, Snapshot snapshot) {
		double sp = snapshot.sp;
		double dmgCoeff = snapshot.spellCoeffDirect;
		double dmgMultiplier = snapshot.directDamageDoneMultiplier;

		return (int) ((base + sp * dmgCoeff) * dmgMultiplier);
	}

	private SpellCalculations() {}
}
