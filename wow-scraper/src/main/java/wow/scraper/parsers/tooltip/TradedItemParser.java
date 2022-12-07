package wow.scraper.parsers.tooltip;

import lombok.Getter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.pve.GameVersion;
import wow.commons.util.parser.Rule;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2022-11-16
 */
@Getter
public class TradedItemParser extends AbstractTooltipParser {
	public TradedItemParser(JsonItemDetailsAndTooltip itemDetailsAndTooltip, GameVersion gameVersion, StatPatternRepository statPatternRepository) {
		super(itemDetailsAndTooltip, gameVersion, statPatternRepository);
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
				ruleRightClickToRead,
				Rule.exact("This Item Begins a Quest", () -> this.itemType = ItemType.QUEST),
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
