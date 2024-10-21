package wow.commons.model.source;

/**
 * User: POlszewski
 * Date: 2021-04-03
 */
public record PvP() implements Source {
	@Override
	public boolean isPvP() {
		return true;
	}

	@Override
	public String toString() {
		return "PvP";
	}
}
