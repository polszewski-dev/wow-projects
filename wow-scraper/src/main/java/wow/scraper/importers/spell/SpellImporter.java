package wow.scraper.importers.spell;

import wow.scraper.importers.WowheadImporter;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.model.WowheadSpellInfo;

import java.io.IOException;
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
	protected List<JsonSpellDetails> fetchDetailsList(String url) throws IOException {
		return getWowheadFetcher().fetchSpellDetails(getGameVersion(), url);
	}

	@Override
	protected boolean isToBeSaved(JsonSpellDetails details) {
		return !getScraperConfig().getIgnoredSpellIds().contains(details.getId());
	}

	@Override
	protected void beforeSave(JsonSpellDetails details) throws IOException {
		fetchTooltip(details);
	}

	private void fetchTooltip(JsonSpellDetails details) throws IOException {
		WowheadSpellInfo info = getWowheadFetcher().fetchSpellTooltip(getGameVersion(), details.getId());
		details.setHtmlTooltip(fixTooltip(info.getTooltip()));
		details.setIcon(info.getIcon());
	}
}
