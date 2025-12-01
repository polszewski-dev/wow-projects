package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;

import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
@Getter
@Setter
public class PeriodicSpellComponentSnapshot {
	private final PeriodicCommand command;

	private int baseAmount;

	private int amount;
	private double amountPct;

	private int power;
	private double powerPct;

	private double coeff;

	public PeriodicSpellComponentSnapshot(PeriodicCommand command) {
		this(command, command.amount());
	}

	public PeriodicSpellComponentSnapshot(PeriodicCommand command, int baseAmount) {
		this.command = command;
		this.baseAmount = baseAmount;
	}

	public double getTickAmount(int tickNo) {
		double periodicAmount = baseAmount;

		periodicAmount += (power * coeff / 100.0) * (1 + powerPct / 100.0);
		periodicAmount += amount;
		periodicAmount *= (1 + amountPct / 100.0);
		periodicAmount *= command.tickScheme().weight(tickNo);

		return periodicAmount / command.numTicks();
	}

	public void increaseEffect(double increasePct) {
		this.amountPct += increasePct;
	}
}
