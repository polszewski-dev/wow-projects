package wow.scraper.exporters.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporters.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.*;
import wow.scraper.parsers.stats.StatPatternRepository;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.QuestInfoRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ItemBaseExporter<T extends AbstractTooltipParser> {
	protected ItemDetailRepository itemDetailRepository;
	protected QuestInfoRepository questInfoRepository;
	protected StatPatternRepository statPatternRepository;
	protected ItemBaseExcelBuilder builder;

	public void init(
			ItemDetailRepository itemDetailRepository,
			QuestInfoRepository questInfoRepository,
			StatPatternRepository statPatternRepository,
			ItemBaseExcelBuilder builder
	) {
		this.itemDetailRepository = itemDetailRepository;
		this.questInfoRepository = questInfoRepository;
		this.statPatternRepository = statPatternRepository;
		this.builder = builder;
	}

	public abstract void exportAll() throws IOException;

	protected void export(WowheadItemCategory category) throws IOException {
		for (Integer itemId : getItemIds(category)) {
			for (GameVersionId gameVersion : GameVersionId.values()) {
				itemDetailRepository
						.getDetail(gameVersion, category, itemId)
						.ifPresent(value -> exportItemDetails(value, gameVersion));
			}
		}
	}

	private void exportItemDetails(JsonItemDetails itemDetails, GameVersionId gameVersion) {
		var parser = createParser(itemDetails, gameVersion);

		parser.parse();
		fixMissingRequiredLevel(parser);
		exportParsedData(parser);

		log.info("Added {} {} [{}]", parser.getItemId(), parser.getName(), gameVersion);
	}

	protected abstract T createParser(JsonItemDetails itemDetails, GameVersionId gameVersion);

	protected abstract void exportParsedData(T parser);

	private void fixMissingRequiredLevel(AbstractTooltipParser parser) {
		if (parser.getRequiredLevel() != null) {
			return;
		}

		for (WowheadQuestInfo questInfo : getQuestInfos(parser)) {
			if (questInfo.getRequiredLevel() != null) {
				parser.setRequiredLevel(questInfo.getRequiredLevel());
			}
			if (questInfo.getRequiredSide() != null) {
				parser.setRequiredSide(questInfo.getRequiredSide());
			}
		}
	}

	private List<WowheadQuestInfo> getQuestInfos(AbstractTooltipParser parser) {
		return parser.getItemDetails().getSourcesOf(WowheadSource.QUEST).stream()
				.map(JsonSourceMore::getTi)
				.filter(Objects::nonNull)
				.map(x -> getWowheadQuestInfo(x, parser.getGameVersion()))
				.flatMap(Optional::stream)
				.toList();
	}

	private Optional<WowheadQuestInfo> getWowheadQuestInfo(Integer questId, GameVersionId gameVersionId) {
		try {
			return questInfoRepository.getQuestInfo(gameVersionId, questId);
		} catch (IOException e) {
			log.error("Error accessing quest info: " + questId, e);
			return Optional.empty();
		}
	}

	private List<Integer> getItemIds(WowheadItemCategory category) {
		return Stream.of(GameVersionId.values())
				.map(gameVersion -> itemDetailRepository.getItemIds(gameVersion, category))
				.flatMap(Collection::stream)
				.distinct()
				.sorted()
				.toList();
	}
}
