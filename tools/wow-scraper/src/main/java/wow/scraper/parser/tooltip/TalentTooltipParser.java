package wow.scraper.parser.tooltip;

import lombok.extern.slf4j.Slf4j;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonSpellDetails;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
@Slf4j
public class TalentTooltipParser extends AbstractSpellTooltipParser {
	public TalentTooltipParser(JsonSpellDetails details, ScraperContext scraperContext) {
		super(details, scraperContext);
	}

	@Override
	protected Rule[] getRules() {
		if (isTalentSpell()) {
			return new Rule[] {
				ruleIgnoreEverything
			};
		}

		return new Rule[] {
				ruleRank,
				ruleReqCLass,
				ruleTalent,
				Rule.regex("\\(Proc chance: (\\d+)%\\)", x -> {}),
				Rule.regex("\\(Proc chance: (\\d+)%, (\\d+)s cooldown\\)", x -> {}),

				Rule.exact("30 yd range", () -> {}),
				Rule.exact("Instant", () -> {}),
				Rule.exact("Level 60", ()  -> {}),
				ruleReqMeleeWeapon,
				ruleReqOneHandedMeleeWeapon,
				ruleReqCatForm,
				ruleDireBearForm,
				ruleAnyBearForm,
				ruleCatBearForm,
				ruleTools,
				ruleTotems,
				ruleNaturesSwiftness1,
				ruleNaturesSwiftness2,
				ruleNaturesSwiftness3,
				ruleRangeRange,
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

		rank = getScraperDatafixes().getRankOverrides().get(getSpellId());

		if (rank == null && details.getRank() != null) {
			ruleRank.matchAndTakeAction(details.getRank());
		}

		if (rank == null) {
			throw new IllegalArgumentException("No rank: %s, id: %s".formatted(name, details.getId()));
		}
	}

	private void parseTalentSpell() {
		AbilityTooltipParser abilityParser = new AbilityTooltipParser(details, getScraperContext());
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
		return getScraperDatafixes().getTalentCalculatorPosition(getCharacterClass(), getName(), getGameVersion());
	}

	public boolean isTalentSpell() {
		return getScraperDatafixes().isTalentSpell(getName(), getCategory(), getGameVersion());
	}
}
