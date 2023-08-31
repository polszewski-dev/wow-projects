package wow.commons.model.source;

import wow.commons.model.profession.ProfessionId;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public record Crafted(ProfessionId professionId) implements Source {
	@Override
	public boolean isCrafted() {
		return true;
	}

	@Override
	public String toString() {
		return professionId != null ? professionId.toString(): "Crafted";
	}
}
