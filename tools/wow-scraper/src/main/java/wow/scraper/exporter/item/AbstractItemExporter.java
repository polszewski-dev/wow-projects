package wow.scraper.exporter.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Side;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonQuestRewardInfo;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.model.WowheadSide;
import wow.scraper.parser.tooltip.AbstractItemTooltipParser;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class AbstractItemExporter<T extends AbstractItemTooltipParser, B extends WowExcelBuilder> extends ItemBaseExporter<WowheadItemCategory, JsonItemDetails, T, B> {
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

		var requiredLevelOverride = getScraperDatafixes().getRequiredLevelOverrides().get(parser.getDetails().getName());

		if (requiredLevelOverride != null) {
			parser.setRequiredLevel(requiredLevelOverride);
			return;
		}

		var questInfos = getQuestRewardInfos(parser);
		var requiredLevel = getRequiredLevel(questInfos);
		var requiredSide = getRequiredSide(questInfos);

		requiredLevel.ifPresent(parser::setRequiredLevel);
		requiredSide.ifPresent(parser::setRequiredSide);
	}

	private List<JsonQuestRewardInfo> getQuestRewardInfos(T parser) {
		return getWowheadFetcher().fetchQuestRewardInfo(
				parser.getGameVersion(),
				"item=" + parser.getDetails().getId()
		);
	}

	private Optional<Integer> getRequiredLevel(List<JsonQuestRewardInfo> questRewardInfos) {
		return questRewardInfos.stream()
				.map(JsonQuestRewardInfo::getReqlevel)
				.min(Comparator.naturalOrder());
	}

	private Optional<Side> getRequiredSide(List<JsonQuestRewardInfo> questRewardInfos) {
		var sides = questRewardInfos.stream()
				.map(JsonQuestRewardInfo::getSide)
				.map(WowheadSide::fromCode)
				.map(wowheadSide -> wowheadSide == WowheadSide.NONE ? WowheadSide.BOTH : wowheadSide)
				.map(WowheadSide::getSides)
				.flatMap(Collection::stream)
				.distinct()
				.toList();

		if (sides.isEmpty() || sides.size() == Side.values().length) {
			return Optional.empty();
		}

		return getUniqueResult(sides);
	}
}
