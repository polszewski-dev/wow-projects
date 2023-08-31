package wow.scraper.exporter.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Slf4j
public abstract class ItemBaseExporter<C, D extends JsonCommonDetails, T extends AbstractTooltipParser<D>> extends ExcelExporter<ItemBaseExcelBuilder> {
	protected List<T> parsers = new ArrayList<>();

	protected void export(C category) {
		for (Integer detailId : getDetailIds(category)) {
			for (GameVersionId gameVersion : getScraperConfig().getGameVersions()) {
				getDetail(category, detailId, gameVersion)
						.ifPresent(details -> exportDetails(details, gameVersion, category));
			}
		}
	}

	private List<Integer> getDetailIds(C category) {
		return getScraperConfig().getGameVersions().stream()
				.map(gameVersion -> getDetailIds(category, gameVersion))
				.flatMap(Collection::stream)
				.distinct()
				.sorted()
				.toList();
	}

	protected abstract List<Integer> getDetailIds(C category, GameVersionId gameVersion);

	protected abstract Optional<D> getDetail(C category, Integer detailId, GameVersionId gameVersion);

	private void exportDetails(D details, GameVersionId gameVersion, C category) {
		var parser = createParser(details, gameVersion);

		parser.parse();
		afterParse(parser, category);
		this.parsers.add(parser);

		log.info("Added {} {} [{}]", parser.getDetails().getId(), parser.getName(), gameVersion);
	}

	protected abstract T createParser(D details, GameVersionId gameVersion);

	protected void afterParse(T parser, C category) {
		fixPhase(parser, category);
	}

	private void fixPhase(T parser, C category) {
		Integer detailId = parser.getDetails().getId();
		GameVersionId gameVersion = parser.getGameVersion();

		if (appearedInPreviousVersion(detailId, gameVersion, category)) {
			parser.setPhase(gameVersion.getPrepatchPhase().orElseThrow());
		}
	}

	private boolean appearedInPreviousVersion(Integer detailId, GameVersionId gameVersion, C category) {
		var previousVersion = gameVersion.getPreviousVersion();

		if (previousVersion.isEmpty()) {
			return false;
		}

		List<GameVersionId> gameVersions = getGameVersions(category, detailId);

		return gameVersions.contains(previousVersion.get());
	}

	private List<GameVersionId> getGameVersions(C category, Integer detailId) {
		return getGameVersionMap(category).getOrDefault(detailId, List.of());
	}

	private Map<Integer, List<GameVersionId>> getGameVersionMap(C category) {
		Map<Integer, List<GameVersionId>> map = new HashMap<>();

		for (GameVersionId gameVersion : getScraperConfig().getGameVersions()) {
			getDetailIds(category, gameVersion).forEach(id -> map.computeIfAbsent(id, x -> new ArrayList<>()).add(gameVersion));
		}

		return map;
	}
}
