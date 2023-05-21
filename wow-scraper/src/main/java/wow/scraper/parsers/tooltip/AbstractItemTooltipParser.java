package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractItemTooltipParser extends AbstractTooltipParser<JsonItemDetails> {
	protected AbstractItemTooltipParser(JsonItemDetails details, GameVersionId gameVersion, StatPatternRepository statPatternRepository) {
		super(details, gameVersion, statPatternRepository);
	}

	public int getItemId() {
		return details.getId();
	}
}
