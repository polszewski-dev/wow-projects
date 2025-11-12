package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.effect.component.PeriodicComponent;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
@Getter
@Setter
public class PeriodicSpellComponentSnapshot {
	private final PeriodicComponent component;

	private int baseAmount;

	private int amount;
	private double amountPct;

	private int power;
	private double powerPct;

	private double coeff;

	public PeriodicSpellComponentSnapshot(PeriodicComponent component) {
		this(component, component.amount());
	}

	public PeriodicSpellComponentSnapshot(PeriodicComponent component, int baseAmount) {
		this.component = component;
		this.baseAmount = baseAmount;
	}

	public double getTickAmount(int tickNo) {
		double periodicAmount = baseAmount;

		periodicAmount += (power * coeff / 100.0) * (1 + powerPct / 100.0);
		periodicAmount += amount;
		periodicAmount *= (1 + amountPct / 100.0);
		periodicAmount *= component.tickScheme().weight(tickNo);

		return periodicAmount / component.numTicks();
	}

	public void increaseEffect(double increasePct) {
		this.amountPct += increasePct;
	}
}
