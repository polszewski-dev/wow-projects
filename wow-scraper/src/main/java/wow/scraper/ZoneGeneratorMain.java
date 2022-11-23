package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadGameVersion;
import wow.scraper.model.WowheadZoneType;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@Slf4j
public class ZoneGeneratorMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new ZoneGeneratorMain().run();
	}

	@Override
	protected void run() throws IOException {
		List<JsonZoneDetails> zones = getWowheadFetcher().fetchZoneDetails(getGameVersion(), "zones").stream()
				.filter(x -> !getScraperConfig().getIgnoredZoneIds().contains(x.getId()))
				.collect(Collectors.toList());

		fixData(zones);

		zones.sort(Comparator.comparing(JsonZoneDetails::getInstance)
						.thenComparing(JsonZoneDetails::getExpansion)
						.thenComparing((JsonZoneDetails x) -> Math.max(x.getReqlevel(), x.getMinlevel()))
						.thenComparing(JsonZoneDetails::getName));

		log.info(
				"{};{};{};{};{};{};{};{};{}",
				"id",
				"name",
				"short_name",
				"type",
				"version",
				"max_players",
				"req_lvl",
				"min_lvl",
				"max_lvl"
		);

		for (JsonZoneDetails zone : zones) {
			log.info(
				"{};{};{};{};{};{};{};{};{}",
				zone.getId(),
				zone.getName(),
				getShortName(zone),
				WowheadZoneType.fromCode(zone.getInstance()).getType(),
				WowheadGameVersion.fromCode(zone.getExpansion()),
				zone.getNplayers(),
				zone.getReqlevel(),
				zone.getMinlevel(),
				zone.getMaxlevel()
			);
		}
	}

	private void fixData(List<JsonZoneDetails> zones) {
		JsonZoneDetails unknown = new JsonZoneDetails();
		unknown.setId(0);
		unknown.setName("UNKNOWN");
		unknown.setExpansion(WowheadGameVersion.VANILLA.getCode());
		unknown.setInstance(WowheadZoneType.NORMAL.getCode());
		zones.add(unknown);

		zones.forEach(zone -> {
			if (zone.getName().equals("Onyxia's Lair")) {
				zone.setInstance(WowheadZoneType.RAID.getCode());
			}
		});
	}

	private String getShortName(JsonZoneDetails zone) {
		String shortName = getScraperConfig().getDungeonShortNames().get(zone.getName());
		return shortName != null ? shortName : "";
	}
}
