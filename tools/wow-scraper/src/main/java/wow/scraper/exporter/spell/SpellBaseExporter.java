package wow.scraper.exporter.spell;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class SpellBaseExporter<B extends WowExcelBuilder> extends ExcelExporter<B> {
	protected List<JsonSpellDetails> getData(WowheadSpellCategory.Type type) {
		return getScraperConfig().getGameVersions().stream()
				.map(gameVersion -> getData(gameVersion, type))
				.flatMap(Collection::stream)
				.toList();
	}

	private List<JsonSpellDetails> getData(GameVersionId gameVersion, WowheadSpellCategory.Type type) {
		return Stream.of(WowheadSpellCategory.notIgnoredValues())
				.filter(x -> x.getType() == type)
				.map(x -> getData(gameVersion, x))
				.flatMap(Collection::stream)
				.toList();
	}

	private List<JsonSpellDetails> getData(GameVersionId gameVersion, WowheadSpellCategory category) {
		return getDetailIds(gameVersion, category).stream()
				.map(detailId -> getDetail(gameVersion, category, detailId).orElseThrow())
				.toList();
	}

	private List<Integer> getDetailIds(GameVersionId gameVersion, WowheadSpellCategory category) {
		return getSpellDetailRepository().getDetailIds(gameVersion, category);
	}

	private Optional<JsonSpellDetails> getDetail(GameVersionId gameVersion, WowheadSpellCategory category, Integer detailId) {
		var detail = getSpellDetailRepository().getDetail(gameVersion, category, detailId);
		detail.ifPresent(this::fixData);
		return detail;
	}

	private void fixData(JsonSpellDetails details) {
		String nameOverride = getScraperDatafixes().getSpellNameOverrides().get(details.getName());

		if (nameOverride != null) {
			details.setName(nameOverride);
		}

		details.setName(details.getName().trim());
	}

	protected boolean isTalentSpell(JsonSpellDetails details) {
		return getScraperDatafixes().isTalentSpell(details.getName(), details.getCategory(), details.getReqVersion());
	}

	protected String getNameVersionKey(JsonSpellDetails x) {
		return x.getName() + x.getReqVersion();
	}
}
