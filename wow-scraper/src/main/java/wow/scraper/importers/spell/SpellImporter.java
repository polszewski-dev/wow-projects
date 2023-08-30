package wow.scraper.importers.spell;

import wow.scraper.importers.WowheadImporter;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.model.WowheadSpellInfo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		var details = getWowheadFetcher().fetchSpellDetails(getGameVersion(), url);
		var missingSpells = getMissingSpells(details);

		return Stream.concat(details.stream(), missingSpells.stream()).toList();
	}

	private List<JsonSpellDetails> getMissingSpells(List<JsonSpellDetails> details) {
		var existingIds = details.stream().map(JsonSpellDetails::getId).collect(Collectors.toSet());
		var missingSpellsIds = getScraperConfig().getMissingSpellIds(getCategory(), getGameVersion()).stream()
				.filter(x -> !existingIds.contains(x))
				.toList();

		return getWowheadFetcher().fetchSpellDetails(getGameVersion(), "spells", missingSpellsIds);
	}

	@Override
	protected boolean isToBeSaved(JsonSpellDetails details) {
		return !getScraperConfig().isSpellIgnored(details.getId(), getCategory(), getGameVersion());
	}

	@Override
	protected void beforeSave(JsonSpellDetails details) {
		fetchTooltip(details);
		details.setReqVersion(getGameVersion());
		details.setCategory(getCategory());
	}

	private void fetchTooltip(JsonSpellDetails details) {
		WowheadSpellInfo info = getWowheadFetcher().fetchSpellTooltip(getGameVersion(), details.getId());
		details.setHtmlTooltip(fixTooltip(info.getTooltip()));
		details.setIcon(info.getIcon());
	}
}
