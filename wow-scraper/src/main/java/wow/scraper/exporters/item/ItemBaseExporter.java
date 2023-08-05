package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.scraper.exporters.ExcelExporter;
import wow.scraper.exporters.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Slf4j
public abstract class ItemBaseExporter<C, D extends JsonCommonDetails, T extends AbstractTooltipParser<D>> extends ExcelExporter<ItemBaseExcelBuilder> {
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
		exportParsedData(parser);

		log.info("Added {} {} [{}]", parser.getDetails().getId(), parser.getName(), gameVersion);
	}

	protected abstract T createParser(D details, GameVersionId gameVersion);

	protected void afterParse(T parser, C category) {
		fixPhase(parser, category);
	}

	protected abstract void exportParsedData(T parser);

	private void fixPhase(T parser, C category) {
		PhaseId correctPhase = getCorrectPhase(parser, category);
		parser.setPhase(correctPhase);
	}

	private PhaseId getCorrectPhase(T parser, C category) {
		Integer detailId = parser.getDetails().getId();
		PhaseId phaseOverride = getPhaseOverride(detailId);
		GameVersionId gameVersion = parser.getGameVersion();

		if (phaseOverride != null && phaseOverride.getGameVersionId() == gameVersion) {
			return phaseOverride;
		}

		if (appearedInPreviousVersion(detailId, gameVersion, category)) {
			return gameVersion.getPrepatchPhase().orElseThrow();
		}

		return parser.getPhase();
	}

	private boolean appearedInPreviousVersion(Integer detailId, GameVersionId gameVersion, C category) {
		var previousVersion = gameVersion.getPreviousVersion();

		if (previousVersion.isEmpty()) {
			return false;
		}

		List<GameVersionId> gameVersions = getGameVersions(category, detailId);

		return gameVersions.contains(previousVersion.get());
	}

	protected abstract PhaseId getPhaseOverride(int id);

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
