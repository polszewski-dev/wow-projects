package wow.commons.model.sources;

import wow.commons.model.professions.ProfessionId;

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
