package wow.simulator.util;

import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.simulator.util.CalcUtils.increaseByPct;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
public record SpellInfo(String name, Direct direct, Periodic periodic, int manaCost, double baseCastTime, double cooldown) {
	public record Direct(int min, int max, double coeff) {
		public double damage(double coeffBonus, int sp) {
			return getSpellDmg(min, max, coeff + coeffBonus, sp);
		}
	}

	public record Periodic(int value, double coeff, double duration, int numTicks) {
		public double damage(double coeffBonus, int sp) {
			return getSpellDmg(value, value, coeff + coeffBonus, sp);
		}

		public double tickDamage() {
			return value / (double) numTicks;
		}
	}

	public SpellInfo(String name, int manaCost, double baseCastTime) {
		this(name, null, null, manaCost, baseCastTime, 0);
	}

	public SpellInfo withDirect(int min, int max, double coeff) {
		return new SpellInfo(name, new Direct(min, max, coeff), periodic, manaCost, baseCastTime, cooldown);
	}

	public SpellInfo withPeriodic(int value, double coeff, double duration, int numTicks) {
		return new SpellInfo(name, direct, new Periodic(value, coeff, duration, numTicks), manaCost, baseCastTime, cooldown);
	}

	public SpellInfo withCooldown(double cooldown) {
		return new SpellInfo(name, direct, periodic, manaCost, baseCastTime, cooldown);
	}

	public double damage() {
		return damage(0, 0);
	}

	public double damage(int sp) {
		return damage(0, sp);
	}

	public double damageIncreasedByPct(int sp, int pctIncrease) {
		return increaseByPct(damage(sp), pctIncrease);
	}

	public double percentOfDamage(int pct) {
		return getPercentOf(pct, damage());
	}

	public double percentOfTickDamage(int pct) {
		return getPercentOf(pct, tickDamage());
	}

	public double damage(double coeffBonus, int sp) {
		return (direct != null ? direct.damage(coeffBonus, sp) : 0) + (periodic != null ? periodic.damage(coeffBonus, sp) : 0);
	}

	public double directDamage() {
		return direct.damage(0, 0);
	}

	public double tickDamage() {
		return periodic.tickDamage();
	}

	public int numTicks() {
		return periodic().numTicks();
	}

	public double duration() {
		return periodic.duration();
	}

	public int manaCostIncreasedByPct(int pctIncrease) {
		return increaseByPct(manaCost, pctIncrease);
	}

	private static double getSpellDmg(int min, int max, double coeff, int sd) {
		return (min + max) / 2.0 + getPercentOf(coeff, sd);
	}
}
