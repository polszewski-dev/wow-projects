package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.pve.GameVersion;
import wow.commons.util.parser.Rule;
import wow.scraper.model.JsonItemDetailsAndTooltip;

/**
 * User: POlszewski
 * Date: 2022-11-16
 */
@Getter
public class TradedItemParser extends AbstractTooltipParser {
	public TradedItemParser(JsonItemDetailsAndTooltip itemDetailsAndTooltip, GameVersion gameVersion) {
		super(itemDetailsAndTooltip, gameVersion);
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
				ruleSellPrice,
				ruleDroppedBy,
				ruleDropChance,
				ruleQuote,
				Rule.exact("This Item Begins a Quest", () -> this.itemType = ItemType.QUEST),
				Rule.exact("<Right Click to Read>", () -> {}),
		};
	}

	@Override
	protected void beforeParse() {
		this.itemType = ItemType.TOKEN;
	}

	@Override
	protected void afterParse() {
		// VOID
	}
}
