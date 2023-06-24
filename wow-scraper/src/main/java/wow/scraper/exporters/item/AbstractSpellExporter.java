package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parsers.tooltip.AbstractSpellTooltipParser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class AbstractSpellExporter<T extends AbstractSpellTooltipParser> extends ItemBaseExporter<WowheadSpellCategory, JsonSpellDetails, T> {
	@Override
	protected List<Integer> getDetailIds(WowheadSpellCategory category, GameVersionId gameVersion) throws IOException {
		return getSpellDetailRepository().getDetailIds(gameVersion, category);
	}

	@Override
	protected Optional<JsonSpellDetails> getDetail(WowheadSpellCategory category, Integer detailId, GameVersionId gameVersion) throws IOException {
		return getSpellDetailRepository().getDetail(gameVersion, category, detailId);
	}

	@Override
	protected void afterParse(T parser) {
		// void
	}
}
