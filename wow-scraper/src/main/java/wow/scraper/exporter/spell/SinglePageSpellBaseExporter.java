package wow.scraper.exporter.spell;

import wow.scraper.exporter.spell.excel.SpellBaseExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parser.tooltip.AbstractSpellTooltipParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-09-19
 */
public abstract class SinglePageSpellBaseExporter<T extends AbstractSpellTooltipParser> extends SpellBaseExporter {
	protected List<T> parsers;

	@Override
	protected void prepareData() {
		this.parsers = getAllData().stream()
				.map(this::createParser)
				.collect(Collectors.toList());

		parsers.forEach(AbstractTooltipParser::parse);
		parsers.removeIf(this::isToBeIgnored);
		parsers.sort(getComparator());
	}

	@Override
	protected void exportPreparedData(SpellBaseExcelBuilder builder) {
		addHeader(builder);
		parsers.forEach(parser -> addRow(parser, builder));
	}

	protected abstract List<JsonSpellDetails> getAllData();

	protected abstract void addHeader(SpellBaseExcelBuilder builder);

	protected abstract void addRow(T parser, SpellBaseExcelBuilder builder);

	protected abstract Comparator<T> getComparator();

	protected abstract T createParser(JsonSpellDetails details);

	protected boolean isToBeIgnored(T t) {
		return false;
	}
}
