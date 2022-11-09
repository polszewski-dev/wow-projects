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
		double hitChance = snapshot.getHitChance();
		double critChance = snapshot.getCritChance();
		double critCoeff = snapshot.getCritCoeff();
		double sp = snapshot.getSp();
		double spMultiplier = snapshot.getSpMultiplier();
		double coeffDirect = snapshot.getSpellCoeffDirect();
		double coeffDoT = snapshot.getSpellCoeffDoT();
		double directDamageDoneMultiplier = snapshot.getDirectDamageDoneMultiplier();
		double dotDamageDoneMultiplier = snapshot.getDotDamageDoneMultiplier();

		Duration castTime = snapshot.getEffectiveCastTime();

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

		result.setSnapshot(snapshot);
		result.setTotalDamage(directDamage + dotDamage);
		result.setCastTime(castTime);
		result.setDps(result.getTotalDamage() / result.getCastTime().getSeconds());
		result.setManaCost(snapshot.getManaCost());
		result.setDpm(result.getTotalDamage() / result.getManaCost());

		return result;
	}

	public static int getScaledValue(int base, Snapshot snapshot) {
		double sp = snapshot.getSp();
		double dmgCoeff = snapshot.getSpellCoeffDirect();
		double dmgMultiplier = snapshot.getDirectDamageDoneMultiplier();

		return (int) ((base + sp * dmgCoeff) * dmgMultiplier);
	}

	private SpellCalculations() {}
}
