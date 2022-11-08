package wow.scraper;

import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadGameVersion;
import wow.scraper.model.WowheadZoneType;
import wow.scraper.repository.WowheadFetcher;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public class ZoneGeneratorMain {
	private static final GameVersion GAME_VERSION = GameVersion.TBC;

	public static void main(String[] args) throws IOException {
		List<JsonZoneDetails> zones = WowheadFetcher.fetchZoneDetails(GAME_VERSION, "zones").stream()
				.filter(x -> !isIgnored(x))
				.collect(Collectors.toList());

		fixData(zones);

		zones.sort(Comparator.comparing(JsonZoneDetails::getInstance)
						.thenComparing(JsonZoneDetails::getExpansion)
						.thenComparing((JsonZoneDetails x) -> Math.max(x.getReqlevel(), x.getMinlevel()))
						.thenComparing(JsonZoneDetails::getName));

		System.out.printf(
				"%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
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
			System.out.printf(
				"%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
				zone.getId(),
				zone.getName(),
				"",
				WowheadZoneType.fromCode(zone.getInstance()).getType(),
				WowheadGameVersion.fromCode(zone.getExpansion()),
				zone.getNplayers(),
				zone.getReqlevel(),
				zone.getMinlevel(),
				zone.getMaxlevel()
			);
		}
	}

	private static void fixData(List<JsonZoneDetails> zones) {
		zones.forEach(zone -> {
			if (zone.getName().equals("Onyxia's Lair")) {
				zone.setInstance(WowheadZoneType.RAID.getCode());
			}
		});
	}

	private static boolean isIgnored(JsonZoneDetails x) {
		return List.of(3477,3817,4076,3948,3711,1397).contains(x.getId());
	}
}
