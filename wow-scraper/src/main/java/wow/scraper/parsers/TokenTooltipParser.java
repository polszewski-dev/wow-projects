package wow.scraper.parsers;

import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2022-11-16
 */
public class TokenTooltipParser extends AbstractTooltipParser {
	public TokenTooltipParser(Integer itemId, String htmlTooltip, GameVersion gameVersion) {
		super(itemId, htmlTooltip, gameVersion);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				ruleRequiresLevel,
				ruleItemLevel,
				ruleBindsWhenPickedUp,
				ruleBindsWhenEquipped,
				ruleClassRestriction,
				ruleSellPrice,
		};
	}

	@Override
	protected void beforeParse() {
		//TODO
	}

	@Override
	protected void afterParse() {
		//TODO
	}
}
