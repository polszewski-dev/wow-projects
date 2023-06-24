package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.model.JsonBossDetails;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@Slf4j
public class BossGeneratorMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new BossGeneratorMain().run();
	}

	@Override
	protected void run() throws IOException {
		List<JsonBossDetails> bosses = getWowheadFetcher().fetchBossDetails(TBC, "npcs/classification:3/react-a:-1/react-h:-1#100").stream()
				.filter(x -> !getScraperConfig().getIgnoredBossIds().contains(x.getId()))
				.toList();

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
}
