package wow.character.model.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
public class Raid<M extends Character> {
	public static final int MAX_PARTIES = 8;

	private final List<Party<M>> parties = new ArrayList<>();

	public Raid() {
		for (int i = 0; i < MAX_PARTIES; ++i) {
			parties.add(new Party<>(this));
		}
	}

	public static <M extends Character> Raid<M> newRaid(M member) {
		var raid = new Raid<M>();
		raid.getFirstParty().add(member);
		return raid;
	}

	public void add(M member) {
		var party = parties.stream()
				.filter(Party::canAddAnotherMember)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No free slots"));

		party.add(member);
	}

	public void add(Collection<? extends M> members) {
		for (var member : members) {
			add(member);
		}
	}

	public List<Party<M>> getParties() {
		return List.copyOf(parties);
	}

	public Party<M> getFirstParty() {
		return parties.getFirst();
	}

	public Party<M> getParty(int idx) {
		return parties.get(idx);
	}

	public int getNumMembers() {
		return parties.stream()
				.mapToInt(Party::getNumMembers)
				.sum();
	}

	public List<M> getMembers() {
		return parties.stream()
				.map(Party::getMembers)
				.flatMap(List::stream)
				.toList();
	}

	public M getFirstMember() {
		return parties.stream()
				.map(Party::getMembers)
				.flatMap(List::stream)
				.findFirst()
				.orElseThrow();
	}

	public void forEach(Consumer<? super M> consumer) {
		for (var party : parties) {
			party.forEach(consumer);
		}
	}

	public void forEachMemberOrPet(Consumer<? super M> consumer) {
		for (var party : parties) {
			party.forEachMemberOrPet(consumer);
		}
	}
}
