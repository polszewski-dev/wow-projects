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

	public void add(Unit member) {
		if (!parties.getLast().canAddAnotherMember()) {
			addNewParty();
		}

		parties.getLast().add(member);
	}

	public Party getFirstParty() {
		return parties.getFirst();
	}

	public Party getParty(int idx) {
		return parties.get(idx);
	}

	public int getNumPlayers() {
		return parties.stream()
				.map(Party::getPlayers)
				.mapToInt(List::size)
				.sum();
	}

	public List<Player> getPlayers() {
		return parties.stream()
				.map(Party::getPlayers)
				.flatMap(List::stream)
				.toList();
	}

	public void forEachRaidMember(Consumer<Unit> consumer) {
		for (var party : parties) {
			party.forEachPartyMember(consumer);
		}
	}

	public void forEachPlayer(Consumer<Player> consumer) {
		for (var party : parties) {
			party.forEachPlayer(consumer);
		}
	}
}
