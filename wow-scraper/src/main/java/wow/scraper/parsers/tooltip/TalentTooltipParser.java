package wow.scraper.parsers.tooltip;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.talents.TalentId;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.repository.SpellPatternRepository;
import wow.scraper.repository.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
@Slf4j
public class TalentTooltipParser extends AbstractSpellTooltipParser {
	private final ScraperConfig scraperConfig;

	public TalentTooltipParser(JsonSpellDetails details, StatPatternRepository statPatternRepository, SpellPatternRepository spellPatternRepository, ScraperConfig scraperConfig) {
		super(details, statPatternRepository, spellPatternRepository);
		this.scraperConfig = scraperConfig;
	}

	@Override
	protected Rule[] getRules() {
		if (isTalentSpell()) {
			return new Rule[] {
				Rule.matches(".*", x -> {})// ignore everything
			};
		}

		return new Rule[] {
				ruleRank,
				ruleReqCLass,
				ruleTalent,
				Rule.regex("\\(Proc chance: (\\d+)%\\)", x -> {}),
				Rule.regex("\\(Proc chance: (\\d+)%, (\\d+)s cooldown\\)", x -> {}),
				ruleDescription
		};
	}

	@Override
	protected void beforeParse() {
		// void
	}

	@Override
	protected void afterParse() {
		if (isTalentSpell()) {
			parseTalentSpell();
		}

		if (rank != null) {
			return;
		}

		rank = scraperConfig.getRankOverrides().get(getSpellId());

		if (rank == null) {
			throw new IllegalArgumentException("No rank: " + name);
		}
	}

	private void parseTalentSpell() {
		AbilityTooltipParser abilityParser = new AbilityTooltipParser(details, getStatPatternRepository(), getSpellPatternRepository());
		abilityParser.parse();
		this.description = abilityParser.getDescription();
		this.rank = abilityParser.rank;
		if (rank == 0) {
			rank = 1;
		}
	}

	@Override
	public String getName() {
		return details.getName();
	}

	public int getMaxRank() {
		return details.getMaxRank();
	}

	public Integer getTalentCalculatorPosition() {
		return scraperConfig.getTalentCalculatorPosition(getGameVersion(), TalentId.parse(getName()));
	}

	public boolean isTalentSpell() {
		return scraperConfig.isTalentSpell(getName(), getCategory(), getGameVersion());
	}
}
