package wow.character.model.snapshot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.commons.model.spell.component.DirectComponent;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
@RequiredArgsConstructor
@Getter
@Setter
public class DirectSpellDamageSnapshot {
	private final DirectComponent component;

	private double critPct;
	private double critCoeff;

	private int damage;
	private double damagePct;

	private int power;
	private double powerPct;

	private double coeff;

	public int getDirectDamage(RngStrategy rngStrategy, boolean addBonus, boolean hadCrit) {
		double directDamage = getBaseDamage(rngStrategy, addBonus);

		directDamage += (power * coeff / 100.0) * (1 + powerPct / 100.0);
		directDamage += damage;
		directDamage *= (1 + damagePct / 100.0);

		if (hadCrit) {
			directDamage *= critCoeff;
		}

		return (int) directDamage;
	}

	private double getBaseDamage(RngStrategy rngStrategy, boolean addBonus) {
		int baseDmgMin = component.min();
		int baseDmgMax = component.max();

		if (addBonus) {
			baseDmgMin += component.bonus().min();
			baseDmgMax += component.bonus().max();
		}

		return rngStrategy.getDamage(baseDmgMin, baseDmgMax);
	}
}
