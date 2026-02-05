package wow.simulator.model.unit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
@Getter
@RequiredArgsConstructor
public class Party {
	public static final int MAX_MEMBERS = 5;

	private final Raid raid;
	private final List<Unit> members = new ArrayList<>();

	public void add(Unit member) {
		if (members.size() == MAX_MEMBERS) {
			throw new IllegalArgumentException("Party has already %s characters".formatted(MAX_MEMBERS));
		}

		if (members.contains(member)) {
			return;
		}

		members.add(member);
		member.setParty(this);
	}

	public void add(Unit... members) {
		for (var member : members) {
			add(member);
		}
	}

	public void remove(Unit member) {
		members.remove(member);
	}

	public List<Player> getPlayers() {
		return members.stream()
				.filter(x -> x instanceof Player)
				.map(x -> (Player) x)
				.toList();
	}

	public boolean canAddAnotherMember() {
		return members.size() < MAX_MEMBERS;
	}

	public void forEachPartyMember(Consumer<Unit> consumer) {
		members.forEach(consumer);
	}

	public void forEachPlayer(Consumer<Player> consumer) {
		getPlayers().forEach(consumer);
	}
}
