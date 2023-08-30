package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parsers.tooltip.AbstractSpellTooltipParser;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class AbstractItemSpellExporter<T extends AbstractSpellTooltipParser> extends ItemBaseExporter<WowheadSpellCategory, JsonSpellDetails, T> {
	@Override
	protected List<Integer> getDetailIds(WowheadSpellCategory category, GameVersionId gameVersion) {
		return getSpellDetailRepository().getDetailIds(gameVersion, category);
	}

	@Override
	protected Optional<JsonSpellDetails> getDetail(WowheadSpellCategory category, Integer detailId, GameVersionId gameVersion) {
		return getSpellDetailRepository().getDetail(gameVersion, category, detailId);
	}

	@Override
	protected PhaseId getPhaseOverride(int id) {
		return getScraperConfig().getSpellPhaseOverrides().get(id);
	}
}
