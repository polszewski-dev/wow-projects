package wow.scraper.exporters.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporters.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonCommonDetails;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;
import wow.scraper.repository.SpellDetailRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ItemBaseExporter<C, D extends JsonCommonDetails, T extends AbstractTooltipParser<D>> {
	protected ItemDetailRepository itemDetailRepository;
	protected SpellDetailRepository spellDetailRepository;
	protected QuestInfoRepository questInfoRepository;
	protected StatPatternRepository statPatternRepository;
	protected ItemBaseExcelBuilder builder;

	public void init(
			ItemDetailRepository itemDetailRepository,
			SpellDetailRepository spellDetailRepository,
			QuestInfoRepository questInfoRepository,
			StatPatternRepository statPatternRepository,
			ItemBaseExcelBuilder builder
	) {
		this.itemDetailRepository = itemDetailRepository;
		this.spellDetailRepository = spellDetailRepository;
		this.questInfoRepository = questInfoRepository;
		this.statPatternRepository = statPatternRepository;
		this.builder = builder;
	}

	public abstract void exportAll() throws IOException;

	protected void export(C category) throws IOException {
		for (Integer detailId : getDetailIds(category)) {
			for (GameVersionId gameVersion : GameVersionId.values()) {
				getDetail(category, detailId, gameVersion)
						.ifPresent(details -> exportDetails(details, gameVersion));
			}
		}
	}

	private List<Integer> getDetailIds(C category) {
		return Stream.of(GameVersionId.values())
				.map(gameVersion -> getDetailIds(category, gameVersion))
				.flatMap(Collection::stream)
				.distinct()
				.sorted()
				.toList();
	}

	protected abstract List<Integer> getDetailIds(C category, GameVersionId gameVersion);

	protected abstract Optional<D> getDetail(C category, Integer detailId, GameVersionId gameVersion) throws IOException;

	private void exportDetails(D details, GameVersionId gameVersion) {
		var parser = createParser(details, gameVersion);

		parser.parse();
		afterParse(parser);
		exportParsedData(parser);

		log.info("Added {} {} [{}]", parser.getDetails().getId(), parser.getName(), gameVersion);
	}

	protected abstract void afterParse(T parser);

	protected abstract T createParser(D details, GameVersionId gameVersion);

	protected abstract void exportParsedData(T parser);
}