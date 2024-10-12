package wow.scraper.parser.tooltip;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonItemDetails;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractItemTooltipParser extends AbstractTooltipParser<JsonItemDetails> {
	protected AbstractItemTooltipParser(JsonItemDetails details, GameVersionId gameVersion, ScraperContext scraperContext) {
		super(details, gameVersion, scraperContext);
	}

	public int getItemId() {
		return details.getId();
	}

	@Override
	protected PhaseId getPhaseOverride() {
		return getScraperDatafixes().getItemPhaseOverrides().get(details.getId());
	}
}
