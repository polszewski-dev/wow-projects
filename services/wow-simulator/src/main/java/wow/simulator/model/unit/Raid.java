package wow.simulator.model.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	public Party getParty(int idx) {
		return parties.get(idx);
	}

	public void forEachRaidMember(Consumer<Unit> consumer) {
		for (var party : parties) {
			party.forEachPartyMember(consumer);
		}
	}
}
