package wow.scraper.importers.spell;

import wow.scraper.importers.WowheadImporter;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.model.WowheadSpellInfo;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-24
 */
public class SpellImporter extends WowheadImporter<WowheadSpellCategory, JsonSpellDetails> {
	public SpellImporter(WowheadSpellCategory category) {
		super(category.getUrl(), category);
	}

	@Override
	protected List<JsonSpellDetails> fetchDetailsList(String url) {
		return getWowheadFetcher().fetchSpellDetails(getGameVersion(), url);
	}

	@Override
	protected boolean isToBeSaved(JsonSpellDetails details) {
		return !getScraperConfig().getIgnoredSpellIds().contains(details.getId());
	}

	@Override
	protected void beforeSave(JsonSpellDetails details) {
		fetchTooltip(details);
	}

	private void fetchTooltip(JsonSpellDetails details) {
		WowheadSpellInfo info = getWowheadFetcher().fetchSpellTooltip(getGameVersion(), details.getId());
		details.setHtmlTooltip(fixTooltip(info.getTooltip()));
		details.setIcon(info.getIcon());
	}
}
