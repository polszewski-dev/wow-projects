package wow.scraper.parser.tooltip;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.talent.TalentTree;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
@Slf4j
public abstract class AbstractSpellTooltipParser extends AbstractTooltipParser<JsonSpellDetails> {
	protected Integer rank;
	protected boolean talent;
	protected String description;

	protected final Rule ruleRank = Rule.regex("Rank (\\d+)", x -> this.rank = x.getInteger(0));
	protected final Rule ruleReqCLass = Rule.regex("Requires (\\S+)", x -> this.requiredClass = List.of(CharacterClassId.parse(x.get(0))));
	protected final Rule ruleTalent = Rule.exact("Talent", () -> this.talent = true);
	protected final Rule ruleDescription = Rule.regex("(.+)", x -> parseTooltip(x.get(0)));

	protected AbstractSpellTooltipParser(JsonSpellDetails details, GameVersionId gameVersion, ScraperContext scraperContext) {
		super(details, gameVersion, scraperContext);
	}

	protected AbstractSpellTooltipParser(JsonSpellDetails details, ScraperContext scraperContext) {
		this(details, details.getReqVersion(), scraperContext);
	}

	public int getSpellId() {
		return details.getId();
	}

	public WowheadSpellCategory getCategory() {
		return getDetails().getCategory();
	}

	public CharacterClassId getCharacterClass() {
		return getCategory().getCharacterClass();
	}

	public TalentTree getTalentTree() {
		return getCategory().getTalentTree();
	}

	private void parseTooltip(String line) {
		if (description != null) {
			this.description += "\n" + line;
			return;
		}
		this.description = line;
	}

	@Override
	protected PhaseId getPhaseOverride() {
		return getScraperDatafixes().getSpellPhaseOverrides().get(details.getId());
	}
}
