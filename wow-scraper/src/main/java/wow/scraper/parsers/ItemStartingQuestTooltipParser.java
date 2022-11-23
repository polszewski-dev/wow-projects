package wow.scraper.parsers;

import lombok.Setter;
import wow.commons.model.pve.GameVersion;
import wow.commons.util.parser.Rule;

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
				quote,
				Rule.exact("This Item Begins a Quest", () -> {}),
				Rule.exact("<Right Click to Read>", () -> {}),
		};
	}

	@Override
	protected void beforeParse() {
		// VOID
	}

	@Override
	protected void afterParse() {
		// VOID
	}
}
