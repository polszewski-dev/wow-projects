package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.TickScheme;

import static wow.commons.model.spell.component.ComponentCommand.ChangeHealthPeriodically;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
@Getter
@Setter
public class PeriodicSpellComponentSnapshot {
	private final TickScheme tickScheme;
	private final int numTicks;

	private int baseAmount;

	private int amount;
	private double amountPct;

	private int power;
	private double powerPct;

	private double coeff;

	public PeriodicSpellComponentSnapshot(ChangeHealthPeriodically command) {
		this(command.amount(), command.numTicks(), command.tickScheme());
	}

	public PeriodicSpellComponentSnapshot(int baseAmount, int numTicks) {
		this(baseAmount, numTicks, TickScheme.DEFAULT);
	}

	public PeriodicSpellComponentSnapshot(int baseAmount, int numTicks, TickScheme tickScheme) {
		this.baseAmount = baseAmount;
		this.tickScheme = tickScheme;
		this.numTicks = numTicks;
	}

	public double getTickAmount(int tickNo) {
		double periodicAmount = baseAmount;

		periodicAmount += (power * coeff / 100.0) * (1 + powerPct / 100.0);
		periodicAmount += amount;
		periodicAmount *= (1 + amountPct / 100.0);
		periodicAmount *= tickScheme.weight(tickNo);

		return periodicAmount / numTicks;
	}

	public void increaseEffect(double increasePct) {
		this.amountPct += increasePct;
	}
}
