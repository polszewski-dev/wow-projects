package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.scraper.model.*;
import wow.scraper.parsers.tooltip.AbstractItemTooltipParser;

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
		return getItemDetailRepository().getDetailIds(gameVersion, category);
	}

	@Override
	protected Optional<JsonItemDetails> getDetail(WowheadItemCategory category, Integer detailId, GameVersionId gameVersion) {
		return getItemDetailRepository().getDetail(gameVersion, category, detailId);
	}

	@Override
	protected void afterParse(T parser, WowheadItemCategory category) {
		super.afterParse(parser, category);
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
			return getQuestInfoRepository().getQuestInfo(gameVersionId, questId);
		} catch (Exception e) {
			log.error("Error accessing quest info: " + questId, e);
			return Optional.empty();
		}
	}

	@Override
	protected PhaseId getPhaseOverride(int id) {
		return getScraperConfig().getItemPhaseOverrides().get(id);
	}
}
