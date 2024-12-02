package wow.minmax.model;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.*;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;

import static java.lang.Math.max;

/**
 * User: POlszewski
 * Date: 2023-11-09
 */
@Getter
@Setter
public class Snapshot {
	private Ability ability;

	private BaseStatsSnapshot base;
	private SpellCastSnapshot cast;
	private SpellCostSnapshot cost;
	private double hitPct;
	private DirectSpellDamageSnapshot direct;
	private PeriodicSpellDamageSnapshot periodic;
	private EffectDurationSnapshot effectDuration;

	public boolean isInstantCast() {
		return cast.isInstantCast();
	}

	public double getEffectiveCastTime() {
		if (ability.isChanneled()) {
			return max(effectDuration.getDuration().getSeconds(), cast.getGcd());
		}
		return max(cast.getCastTime(), cast.getGcd());
	}

	public double getEffectiveCooldown() {
		if (hasDotComponent()) {
			return max(getCooldown(), getDuration());
		}
		return getCooldown();
	}

	public double getDuration() {
		return effectDuration != null ? effectDuration.getDuration().getSeconds() : 0;
	}

	public double getCooldown() {
		return cost.getCooldown();
	}

	public boolean hasDotComponent() {
		return periodic != null;
	}

	public double getTotalDamage() {
		double result = 0;

		if (direct != null) {
			result += getDirectDamage();
		}

		if (periodic != null) {
			result += getPeriodicDamage();
		}

		return result * (hitPct / 100);
	}

	private double getDirectDamage() {
		boolean addBonus = direct.getComponent().bonus() != null;
		int critDamage = direct.getDirectDamage(RngStrategy.AVERAGED, addBonus, true);
		int nonCritDamage = direct.getDirectDamage(RngStrategy.AVERAGED, addBonus, false);
		double critChance = direct.getCritPct() / 100;

		return critDamage * critChance + nonCritDamage * (1 - critChance);
	}

	private double getPeriodicDamage() {
		double result = 0;
		int tickCount = getTickCount();

		for (int tickNo = 1; tickNo <= tickCount; ++tickNo) {
			result += periodic.getTickDamage(tickNo);
		}

		return result;
	}

	private int getTickCount() {
		return (int) (effectDuration.getDuration().getSeconds() / effectDuration.getTickInterval().getSeconds());
	}

	public int getManaCost() {
		return cost.getResourceType() == ResourceType.MANA ? cost.getCost() : 0;
	}

	public int getPower() {
		var directPower = direct != null ? direct.getPower() : 0;
		var dotPower = periodic != null ? periodic.getPower() : 0;

		return max(directPower, dotPower);
	}

	public double getCritPct() {
		return direct != null ? direct.getCritPct() : 0;
	}

	public double getHastePct() {
		return cast.getHastePct();
	}

	public double getCoeffDirect() {
		return direct != null ? direct.getCoeff() : 0;
	}

	public double getCoeffDoT() {
		return periodic != null ? periodic.getCoeff() : 0;
	}

	public double getCritCoeff() {
		return direct != null ? direct.getCritCoeff() : 0;
	}
}
