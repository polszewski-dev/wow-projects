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
public class DirectSpellComponentSnapshot {
	private final DirectComponent component;

	private double critPct;
	private double critCoeff;

	private int amount;
	private double amountPct;

	private int power;
	private double powerPct;

	private double coeff;

	public int getDirectAmount(RngStrategy rngStrategy, boolean addBonus, boolean hadCrit) {
		double directAmount = getBaseAmount(rngStrategy, addBonus);

		directAmount += (power * coeff / 100.0) * (1 + powerPct / 100.0);
		directAmount += amount;
		directAmount *= (1 + amountPct / 100.0);

		if (hadCrit) {
			directAmount *= critCoeff;
		}

		return (int) directAmount;
	}

	private double getBaseAmount(RngStrategy rngStrategy, boolean addBonus) {
		int baseAmountMin = component.min();
		int baseAmountMax = component.max();

		if (addBonus) {
			baseAmountMin += component.bonus().min();
			baseAmountMax += component.bonus().max();
		}

		return rngStrategy.getAmount(baseAmountMin, baseAmountMax);
	}
}
