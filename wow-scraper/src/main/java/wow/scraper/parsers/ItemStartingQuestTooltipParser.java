package wow.scraper.parsers;

import lombok.Setter;
import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2022-11-16
 */
@Setter
public class ItemStartingQuestTooltipParser extends AbstractTooltipParser {
	public ItemStartingQuestTooltipParser(Integer itemId, String htmlTooltip, GameVersion gameVersion) {
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
				ruleUnique,
				ruleClassRestriction,
				ruleProfessionRestriction,
				ruleDroppedBy,
				ruleSellPrice,
				Rule.exact("This Item Begins a Quest", () -> {}),
				Rule.regex("<Right Click to Read>", params -> {}),
				Rule.regex("\".*\"", params -> {}),
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
