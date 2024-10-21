package wow.character.model.snapshot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.commons.model.effect.component.PeriodicComponent;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
@RequiredArgsConstructor
@Getter
@Setter
public class PeriodicSpellDamageSnapshot {
	private final PeriodicComponent component;

	private int damage;
	private double damagePct;

	private int power;
	private double powerPct;

	private double coeff;

	public double getTickDamage(int tickNo) {
		double periodicDamage = component.amount();

		periodicDamage += (power * coeff / 100.0) * (1 + powerPct / 100.0);
		periodicDamage += damage;
		periodicDamage *= (1 + damagePct / 100.0);
		periodicDamage *= component.tickScheme().weight(tickNo);

		return periodicDamage / component.numTicks();
	}
}
