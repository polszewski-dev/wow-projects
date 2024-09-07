package wow.scraper.exporter.spell;

import wow.scraper.exporter.spell.excel.TalentExcelBuilder;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory.Type;
import wow.scraper.parser.tooltip.TalentTooltipParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class TalentExporter extends SinglePageSpellBaseExporter<TalentTooltipParser, TalentExcelBuilder> {
	@Override
	protected void addHeader(TalentExcelBuilder builder) {
		builder.addTalentHeader();
	}

	@Override
	protected void addRow(TalentTooltipParser parser, TalentExcelBuilder builder) {
		builder.addTalent(parser);
	}

	@Override
	protected List<JsonSpellDetails> getAllData() {
		var allData = getData(Type.TALENT);
		fillMaxRank(allData);
		return allData;
	}

	private void fillMaxRank(List<JsonSpellDetails> allData) {
		var groups = allData.stream().collect(
				Collectors.groupingBy(this::getNameVersionKey)
		);

		groups.values().forEach(this::setMaxRank);
	}

	private void setMaxRank(List<JsonSpellDetails> details) {
		details.forEach(x -> x.setMaxRank(details.size()));
	}

	@Override
	protected boolean isToBeIgnored(TalentTooltipParser parser) {
		return parser.isTalentSpell() && parser.getRank() != 1;
	}

	@Override
	protected Comparator<TalentTooltipParser> getComparator() {
		return Comparator
				.comparing(TalentTooltipParser::getCharacterClass)
				.thenComparing(TalentTooltipParser::getGameVersion)
				.thenComparing(TalentTooltipParser::getTalentCalculatorPosition)
				.thenComparing(TalentTooltipParser::getRank);
	}

	@Override
	protected TalentTooltipParser createParser(JsonSpellDetails details) {
		return new TalentTooltipParser(details, getScraperContext());
	}
}
