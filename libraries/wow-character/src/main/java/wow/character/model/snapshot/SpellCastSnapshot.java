package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-10-17
 */
@Getter
@Setter
public class SpellCastSnapshot {
	private double hastePct;

	private double castTime;
	private double gcd;

	public boolean isInstantCast() {
		return castTime == 0;
	}
}
