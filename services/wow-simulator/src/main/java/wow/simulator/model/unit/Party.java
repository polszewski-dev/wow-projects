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
	public static final int MAX_CHARACTERS = 5;

	private final Raid raid;
	private final List<Unit> players = new ArrayList<>();

	public void add(Unit player) {
		if (players.size() == MAX_CHARACTERS) {
			throw new IllegalArgumentException("Party has already %s characters".formatted(MAX_CHARACTERS));
		}

		players.add(player);
		player.setParty(this);
	}

	public void forEachPartyMember(Consumer<Unit> consumer) {
		players.forEach(consumer);
	}
}
