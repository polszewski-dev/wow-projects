package wow.scraper.exporter.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Slf4j
public abstract class ItemBaseExporter<C, D extends JsonCommonDetails, T extends AbstractTooltipParser<D>, B extends WowExcelBuilder> extends ExcelExporter<B> {
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
		// optional
	}
}
