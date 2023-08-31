package wow.scraper.exporters.spell;

import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory.Type;
import wow.scraper.parsers.tooltip.TalentTooltipParser;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class TalentRankExporter extends SpellBaseExporter<TalentTooltipParser> {
	@Override
	protected void addHeader() {
		builder.addTalentRankHeader();
	}

	@Override
	protected void addRow(TalentTooltipParser parser) {
		builder.addTalentRank(parser);
	}

	@Override
	protected List<JsonSpellDetails> getAllData() {
		return getData(Type.TALENT).stream()
				.toList();
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
