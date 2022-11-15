package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonBossDetails;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@Slf4j
public class BossGeneratorMain extends ScraperTool {
	private static final GameVersion GAME_VERSION = GameVersion.TBC;

	public static void main(String[] args) throws Exception {
		new BossGeneratorMain().run();
	}

	@Override
	protected void run() throws Exception {
		List<JsonBossDetails> bosses = getWowheadFetcher().fetchBossDetails(GAME_VERSION, "npcs/classification:3/react-a:-1/react-h:-1#100").stream()
				.filter(x -> !isIgnored(x))
				.collect(Collectors.toList());

		fixData(bosses);

		bosses.sort(Comparator.comparing(JsonBossDetails::getName));

		log.info(
				"{};{};{}",
				"id",
				"name",
				"zone"
		);

		for (JsonBossDetails boss : bosses) {
			log.info(
					"{};{};{}",
					boss.getId(),
					boss.getName(),
					boss.getLocation().stream().map(Object::toString).collect(Collectors.joining(":"))
			);
		}
	}

	private void fixData(List<JsonBossDetails> bosses) {
		bosses.forEach(boss -> {
			if (boss.getName().equals("Onyxia")) {
				boss.setLocation(List.of(2159));
			} else {
				boss.getLocation().removeIf(x -> x < 0);
			}
		});
	}

	private boolean isIgnored(JsonBossDetails x) {
		return List.of(15963).contains(x.getId());
	}
}
