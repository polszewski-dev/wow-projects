package wow.scraper.parsers;

import wow.commons.model.professions.Profession;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.JsonSourceMore;
import wow.scraper.model.WowheadSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		if (isFactionReward()) {
			return List.of("Faction");
		}

		switch (sources.get(0)) {
			case crafted:
				return parseSingleSourceCrafted();
			case pvp_arena:
				return parseSingleSourcePvP();
			case badges:
				return parseSingleBadges();
			case quest:
				return parseSingleQuest();
			case drop:
				return parseSingleSourceDrop();
			default:
				throw new IllegalArgumentException("Unhandled source: " + sources.get(0));
		}
	}

	private List<String> parseSingleSourceDrop() {
		if (hasNoSourceMores()) {
			return List.of("World Drop");
		}

		return List.of("Drop " + sourceMores);
	}

	private List<String> parseSingleSourceCrafted() {
		if (hasNoSourceMores()) {
			throw new IllegalArgumentException("No source more: " + getName());
		}
		JsonSourceMore sourceMore = sourceMores.get(0);
		switch (sourceMore.getS()) {
			case 197: return List.of(Profession.Tailoring.name());
			case 165: return List.of(Profession.Leatherworking.name());
			case 164: return List.of(Profession.Blacksmithing.name());
			case 171: return List.of(Profession.Alchemy.name());
			case 202: return List.of(Profession.Engineering.name());
			case 755: return List.of(Profession.Jewelcrafting.name());
			default:
				throw new IllegalArgumentException(sourceMores.toString());
		}
	}

	private List<String> parseSingleSourcePvP() {
		return List.of("PvP");
	}

	private List<String> parseSingleBadges() {
		return List.of("Badges");
	}

	private List<String> parseSingleQuest() {
		if (hasNoSourceMores()) {
			return List.of("Quest 2+");
		}

		JsonSourceMore sourceMore = sourceMores.get(0);

		// 2 or more quests
		if (sourceMore.getN() == null) {
			return List.of("Quest: 2+");
		}

		return List.of("Quest: " + sourceMore.getN());
	}

	private String getName() {
		return itemDetailsAndTooltip.getDetails().getName();
	}

	private boolean hasNoSourceMores() {
		return sourceMores == null || sourceMores.isEmpty();
	}

	private boolean isFactionReward() {
		String tooltip = itemDetailsAndTooltip.getHtmlTooltip();
		return Stream.of(" - Neutral", " - Friendly", " - Honored", " - Revered", " - Exalted").anyMatch(tooltip::contains);
	}

	private static boolean isPvPItemName(String name) {
		return PVP_ITEM_NAME_PARTS.stream().anyMatch(name::contains);
	}
}
