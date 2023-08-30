package wow.scraper.exporters.spell;

import wow.scraper.model.JsonSpellDetails;
import wow.scraper.parsers.tooltip.AbilityTooltipParser;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class AbilityExporter extends AbilityRankExporter {
	@Override
	protected void addHeader() {
		builder.addAbilityHeader();
	}

	@Override
	protected void addRow(AbilityTooltipParser parser) {
		builder.addAbility(parser);
	}

	@Override
	protected List<JsonSpellDetails> getAllData() {
		return stripRankInfo(super.getAllData());
	}

	@Override
	protected Comparator<AbilityTooltipParser> getComparator() {
		return Comparator
				.comparing(AbilityTooltipParser::getCharacterClass)
				.thenComparing(AbilityTooltipParser::getTalentTree)
				.thenComparing(AbilityTooltipParser::getName)
				.thenComparing(AbilityTooltipParser::getGameVersion);
	}
}
