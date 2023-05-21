package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.*;
import wow.scraper.parsers.tooltip.AbstractItemTooltipParser;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class AbstractItemExporter<T extends AbstractItemTooltipParser> extends ItemBaseExporter<WowheadItemCategory, JsonItemDetails, T> {
	@Override
	protected List<Integer> getDetailIds(WowheadItemCategory category, GameVersionId gameVersion) {
		return itemDetailRepository.getItemIds(gameVersion, category);
	}

	@Override
	protected Optional<JsonItemDetails> getDetail(WowheadItemCategory category, Integer detailId, GameVersionId gameVersion) throws IOException {
		return itemDetailRepository.getDetail(gameVersion, category, detailId);
	}

	@Override
	protected void afterParse(T parser) {
		fixMissingRequiredLevel(parser);
	}

	private void fixMissingRequiredLevel(T parser) {
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

	private List<WowheadQuestInfo> getQuestInfos(T parser) {
		return parser.getDetails().getSourcesOf(WowheadSource.QUEST).stream()
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
}
