package wow.simulator.model.unit;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
public class Raid {
	public static final int MAX_PARTIES = 8;

	private final List<Party> parties = new ArrayList<>();

	public Raid() {
		addNewParty();
	}

	public void addNewParty() {
		if (parties.size() == MAX_PARTIES) {
			throw new IllegalArgumentException("Raid has already %s parties".formatted(MAX_PARTIES));
		}

		parties.add(new Party(this));
	}

	public Party getFirstParty() {
		return parties.getFirst();
	}
}
