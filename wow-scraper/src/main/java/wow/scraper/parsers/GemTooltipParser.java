package wow.scraper.parsers;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.pve.GameVersion;
import wow.commons.repository.impl.parsers.gems.GemStatsParser;
import wow.commons.util.parser.Rule;
import wow.scraper.model.JsonItemDetailsAndTooltip;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public class GemTooltipParser extends AbstractTooltipParser {
	private GemColor color;
	private List<MetaEnabler> metaEnablers;
	private Attributes stats;

	public GemTooltipParser(JsonItemDetailsAndTooltip itemDetailsAndTooltip, GameVersion gameVersion) {
		super(itemDetailsAndTooltip, gameVersion);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				ruleItemLevel,
				Rule.tryParse(GemTooltipParser::tryParseColor, x -> this.color = x),
				ruleBindsWhenPickedUp,
				ruleBindsWhenEquipped,
				ruleUnique,
				ruleUniqueEquipped,
				Rule.tryParse(MetaEnabler::tryParse, x -> metaEnablers.add(x)),
				ruleProfessionRestriction,
				ruleSellPrice,
				Rule.tryParse(GemStatsParser::tryParseStats, x -> stats = x),
		};
	}

	@Override
	protected void beforeParse() {
		this.itemType = ItemType.GEM;
		this.metaEnablers = new ArrayList<>();
	}

	@Override
	protected void afterParse() {
		if (color == null) {
			throw new IllegalArgumentException("Couldn't parse color for: " + name);
		}

		if (stats.isEmpty()) {
			throw new IllegalArgumentException("Couldn't parse any stat for: " + name);
		}
	}

	private static GemColor tryParseColor(String line) {
		if (line.equals("\"Matches a Red Socket.\"") || line.equals("\"Matches a Red Socket.  Socketing this gem causes the item to become Soulbound.\"")) {
			return GemColor.RED;
		}
		if (line.equals("\"Matches a Yellow Socket.\"") || line.equals("\"Matches a Yellow Socket.  Socketing this gem causes the item to become Soulbound.\"")) {
			return GemColor.YELLOW;
		}
		if (line.equals("\"Matches a Blue Socket.\"") || line.startsWith("\"Matches a Blue Socket.  Socketing this gem causes the item to become Soulbound.\"")) {
			return GemColor.BLUE;
		}
		if (line.equals("\"Matches a Red or Yellow Socket.\"") || line.equals("\"Matches a Yellow or Red Socket.\"")) {
			return GemColor.ORANGE;
		}
		if (line.equals("\"Matches a Red or Blue Socket.\"")) {
			return GemColor.PURPLE;
		}
		if (line.equals("\"Matches a Yellow or Blue Socket.\"") || line.equals("\"Matches a Blue or Yellow Socket.\"")) {
			return GemColor.GREEN;
		}
		if (line.equals("\"Matches a Red, Yellow or Blue Socket.\"")) {
			return GemColor.PRISMATIC;
		}
		if (line.equals("\"Only fits in a meta gem slot.\"")) {
			return GemColor.META;
		}
		return null;
	}
}
