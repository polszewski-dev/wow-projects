package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.repository.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractSpellTooltipParser extends AbstractTooltipParser<JsonSpellDetails> {
	protected AbstractSpellTooltipParser(JsonSpellDetails details, GameVersionId gameVersion, StatPatternRepository statPatternRepository) {
		super(details, gameVersion, statPatternRepository);
	}

	public int getSpellId() {
		return details.getId();
	}
}
