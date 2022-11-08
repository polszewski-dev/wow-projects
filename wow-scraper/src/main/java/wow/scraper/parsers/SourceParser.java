package wow.scraper.parsers;

import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.JsonSourceMore;
import wow.scraper.model.WowheadProfession;
import wow.scraper.model.WowheadSource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
public class SourceParser {
	private static final List<String> PVP_ITEM_NAME_PARTS = List.of(
			"Gladiator's",
			"Vindicator's",
			"Guardian's",
			"Sergeant's",
			"Veteran's",
			"Medallion of the Horde",
			"Medallion of the Alliance",
			"Insignia of the Horde",
			"Insignia of the Alliance"
	);

	private final JsonItemDetailsAndTooltip itemDetailsAndTooltip;
	private final List<WowheadSource> sources;
	private final List<JsonSourceMore> sourceMores;

	public SourceParser(JsonItemDetailsAndTooltip itemDetailsAndTooltip) {
		this.itemDetailsAndTooltip = itemDetailsAndTooltip;
		this.sources = itemDetailsAndTooltip.getDetails().getSources()
				.stream()
				.map(WowheadSource::fromCode)
				.collect(Collectors.toList());
		this.sourceMores = itemDetailsAndTooltip.getDetails().getSourceMores();
	}

	//TODO head of onyxia etc
	public List<String> getSource() {
		if (sources.size() != 1) {
			throw new IllegalArgumentException("Multiple sources for: " + getName());
		}

		if (isPvPItemName(getName())) {
			return List.of("PvP");
		}

		switch (sources.get(0)) {
			case CRAFTED:
				return List.of(parseSingleSourceCrafted());
			case PVP_ARENA:
				return List.of(parseSingleSourcePvP());
			case BADGES:
				return List.of(parseSingleBadges());
			case QUEST:
				return List.of(parseSingleQuest());
			case DROP:
				return List.of(parseSingleSourceDrop());
			default:
				throw new IllegalArgumentException("Unhandled source: " + sources.get(0));
		}
	}

	private String parseSingleSourceDrop() {
		if (hasNoSourceMores()) {
			return "WorldDrop";
		}

		JsonSourceMore sourceMore = assertSingleSourcMore();

		if (sourceMore.getN() != null) {

			return String.format("BossDrop:%s:%s", sourceMore.getN(), sourceMore.getZ() != null ? sourceMore.getZ() : 0);
		}

		return "ZoneDrop:" + sourceMore.getZ();
	}

	private String parseSingleSourceCrafted() {
		if (hasNoSourceMores()) {
			throw new IllegalArgumentException("No source more: " + getName());
		}

		JsonSourceMore sourceMore = assertSingleSourcMore();

		return "Crafted:" + WowheadProfession.fromCode(sourceMore.getS()).getProfession();
	}

	private String parseSingleSourcePvP() {
		return "PvP";
	}

	private String parseSingleBadges() {
		return "Badges";
	}

	private String parseSingleQuest() {
		if (hasNoSourceMores()) {
			return "Quest:>1";
		}

		JsonSourceMore sourceMore = assertSingleSourcMore();

		// 2 or more quests
		if (sourceMore.getN() == null) {
			return "Quest:>1";
		}

		return "Quest:" + sourceMore.getN();
	}

	private String getName() {
		return itemDetailsAndTooltip.getDetails().getName();
	}

	private boolean hasNoSourceMores() {
		return sourceMores == null || sourceMores.isEmpty();
	}

	private JsonSourceMore assertSingleSourcMore() {
		if (sourceMores.size() != 1) {
			throw new IllegalArgumentException("Multiple source mores: " + sourceMores);
		}
		JsonSourceMore sourceMore = sourceMores.get(0);
		fixData(sourceMore);
		return sourceMore;
	}

	private void fixData(JsonSourceMore sourceMore) {
		if ("Onyxia".equals(sourceMore.getN()) && sourceMore.getZ() == null) {
			sourceMore.setZ(2159);
		}
	}

	private static boolean isPvPItemName(String name) {
		return PVP_ITEM_NAME_PARTS.stream().anyMatch(name::contains);
	}
}
