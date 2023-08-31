package wow.scraper.exporter.spell;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.spell.excel.SpellBaseExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parser.tooltip.AbstractSpellTooltipParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@Slf4j
public abstract class SpellBaseExporter<T extends AbstractSpellTooltipParser> extends ExcelExporter<SpellBaseExcelBuilder> {
	@Override
	public void exportAll() {
		addHeader();

		var parsers = getAllData().stream()
				.map(this::createParser)
				.collect(Collectors.toList());

		parsers.forEach(AbstractTooltipParser::parse);
		parsers.removeIf(this::isToBeIgnored);
		parsers.sort(getComparator());
		parsers.forEach(this::addRow);
	}

	protected abstract void addHeader();

	protected abstract void addRow(T parser);

	protected abstract Comparator<T> getComparator();

	protected abstract T createParser(JsonSpellDetails details);

	protected abstract List<JsonSpellDetails> getAllData();

	protected boolean isToBeIgnored(T t) {
		return false;
	}

	protected List<JsonSpellDetails> getData(WowheadSpellCategory.Type type) {
		return getScraperConfig().getGameVersions().stream()
				.map(gameVersion -> getData(gameVersion, type))
				.flatMap(Collection::stream)
				.toList();
	}

	private List<JsonSpellDetails> getData(GameVersionId gameVersion, WowheadSpellCategory.Type type) {
		return Stream.of(WowheadSpellCategory.values())
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
		String nameOverride = getScraperConfig().getSpellNameOverrides().get(details.getName());

		if (nameOverride != null) {
			details.setName(nameOverride);
		}

		details.setName(details.getName().trim());
	}

	protected boolean isTalentSpell(JsonSpellDetails details) {
		return getScraperConfig().isTalentSpell(details.getName(), details.getCategory(), details.getReqVersion());
	}

	protected List<JsonSpellDetails> stripRankInfo(List<JsonSpellDetails> data) {
		var groups = data.stream()
				.collect(Collectors.groupingBy(
						this::getNameVersionKey,
						Collectors.reducing(null, Function.identity(), (first, last) -> last)
				));

		return groups.values().stream().toList();
	}

	protected String getNameVersionKey(JsonSpellDetails x) {
		return x.getName() + x.getReqVersion();
	}
}
