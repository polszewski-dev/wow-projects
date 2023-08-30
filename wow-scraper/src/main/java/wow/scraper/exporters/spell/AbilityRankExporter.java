package wow.scraper.exporters.spell;

import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory.Type;
import wow.scraper.parsers.tooltip.AbilityTooltipParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class AbilityRankExporter extends SpellBaseExporter<AbilityTooltipParser> {
	@Override
	protected void addHeader() {
		builder.addAbilityRankHeader();
	}

	@Override
	protected void addRow(AbilityTooltipParser parser) {
		builder.addAbilityRank(parser);
	}

	@Override
	protected List<JsonSpellDetails> getAllData() {
		var abilities = getData(Type.ABILITY).stream();

		var talentSpells = getData(Type.TALENT).stream()
				.filter(this::isTalentSpell);

		return Stream.concat(abilities, talentSpells).toList();
	}

	@Override
	protected Comparator<AbilityTooltipParser> getComparator() {
		return Comparator
				.comparing(AbilityTooltipParser::getCharacterClass)
				.thenComparing(AbilityTooltipParser::getTalentTree)
				.thenComparing(AbilityTooltipParser::getName)
				.thenComparing(AbilityTooltipParser::getGameVersion)
				.thenComparing(AbilityTooltipParser::getRank);
	}

	@Override
	protected AbilityTooltipParser createParser(JsonSpellDetails details) {
		return new AbilityTooltipParser(details, getStatPatternRepository(), getSpellPatternRepository());
	}
}
