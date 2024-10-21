package wow.scraper.exporter.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parser.tooltip.AbstractSpellTooltipParser;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class AbstractItemSpellExporter<T extends AbstractSpellTooltipParser, B extends WowExcelBuilder> extends ItemBaseExporter<WowheadSpellCategory, JsonSpellDetails, T, B> {
	@Override
	protected List<Integer> getDetailIds(WowheadSpellCategory category, GameVersionId gameVersion) {
		return getSpellDetailRepository().getDetailIds(gameVersion, category);
	}

	@Override
	protected Optional<JsonSpellDetails> getDetail(WowheadSpellCategory category, Integer detailId, GameVersionId gameVersion) {
		return getSpellDetailRepository().getDetail(gameVersion, category, detailId);
	}
}
