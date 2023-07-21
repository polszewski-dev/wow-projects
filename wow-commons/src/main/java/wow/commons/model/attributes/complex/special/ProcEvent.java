package wow.commons.model.attributes.complex.special;

import wow.commons.model.Percent;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-04-08
 */
public record ProcEvent(
		ProcEventType type,
		Percent chance
) {
	public ProcEvent {
		Objects.requireNonNull(type);
		Objects.requireNonNull(chance);
	}

	public double getProcChance(double hitChance, double critChance) {
		return type.getProcChance(hitChance, critChance) * chance.getCoefficient();
	}
}
