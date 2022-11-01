package wow.scraper;

import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonBossDetails;
import wow.scraper.repository.WowheadFetcher;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public class BossGeneratorMain {
	private static final GameVersion GAME_VERSION = GameVersion.TBC;

	public static void main(String[] args) throws IOException {
		List<JsonBossDetails> bosses = WowheadFetcher.fetchBossDetails(GAME_VERSION, "npcs/classification:3/react-a:-1/react-h:-1#100").stream()
				.filter(x -> !isIgnored(x))
				.collect(Collectors.toList());

		fixData(bosses);

		bosses.sort(Comparator.comparing(JsonBossDetails::getName));

		System.out.printf(
				"%s;%s;%s%n",
				"id",
				"name",
				"zone"
		);

		for (JsonBossDetails boss : bosses) {
			System.out.printf(
					"%s;%s;%s%n",
					boss.getId(),
					boss.getName(),
					boss.getLocation().stream().map(Object::toString).collect(Collectors.joining(":"))
			);
		}
	}

	private static void fixData(List<JsonBossDetails> bosses) {
		bosses.forEach(boss -> {
			if (boss.getName().equals("Onyxia")) {
				boss.setLocation(List.of(2159));
			} else {
				boss.getLocation().removeIf(x -> x < 0);
			}
		});
	}

	private static boolean isIgnored(JsonBossDetails x) {
		return List.of(15963).contains(x.getId());
	}
}
