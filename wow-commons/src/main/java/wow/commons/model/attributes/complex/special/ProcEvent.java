package wow.commons.model.attributes.complex.special;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.Percent;

/**
 * User: POlszewski
 * Date: 2023-04-08
 */
@AllArgsConstructor
@Getter
public class ProcEvent {
	@NonNull
	private final ProcEventType type;
	@NonNull
	private final Percent chance;

	public double getProcChance(double hitChance, double critChance) {
		return switch (type) {
			case SPELL_HIT -> hitChance;
			case SPELL_CRIT -> critChance;
			case SPELL_RESIST -> 1 - hitChance;
			case SPELL_DAMAGE -> 1;
		};
	}
}
