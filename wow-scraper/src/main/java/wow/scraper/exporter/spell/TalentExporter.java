package wow.scraper.exporter.spell;

import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parser.tooltip.TalentTooltipParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class TalentExporter extends TalentRankExporter {
	@Override
	protected void addHeader() {
		builder.addTalentHeader();
	}

	@Override
	protected void addRow(TalentTooltipParser parser) {
		builder.addTalent(parser);
	}

	@Override
	protected List<JsonSpellDetails> getAllData() {
		var allData = super.getAllData();
		fillMaxRank(allData);
		return stripRankInfo(allData);
	}

	@Override
	protected Comparator<TalentTooltipParser> getComparator() {
		return Comparator
				.comparing(TalentTooltipParser::getCharacterClass)
				.thenComparing(TalentTooltipParser::getGameVersion)
				.thenComparing(TalentTooltipParser::getTalentCalculatorPosition);
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
}
