package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.professions.ProfessionId;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class Crafted extends Source {
	private final ProfessionId professionId;

	@Override
	public boolean isCrafted() {
		return true;
	}

	@Override
	public String toString() {
		return professionId != null ? professionId.toString(): "Crafted";
	}
}
