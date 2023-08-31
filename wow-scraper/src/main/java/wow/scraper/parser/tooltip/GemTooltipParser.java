package wow.scraper.parser.tooltip;

import lombok.Getter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.parser.Rule;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.parser.effect.ItemStatParser;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public class GemTooltipParser extends AbstractItemTooltipParser {
	private GemColor color;
	private List<MetaEnabler> metaEnablers;
	private ItemStatParser itemStatParser;

	public GemTooltipParser(JsonItemDetails itemDetails, GameVersionId gameVersion, ScraperContext scraperContext) {
		super(itemDetails, gameVersion, scraperContext);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				rulePhase,
				ruleItemLevel,
				ruleBindsWhenPickedUp,
				ruleBindsWhenEquipped,
				ruleUnique,
				ruleUniqueEquipped,
				ruleProfessionRestriction,
				ruleSellPrice,
				Rule.tryParse(GemTooltipParser::tryParseColor, x -> this.color = x),
				Rule.tryParse(MetaEnabler::tryParse, x -> metaEnablers.add(x)),
				Rule.tryParse(this::tryParseItemEffect, x -> {}),
		};
	}

	@Override
	protected void beforeParse() {
		this.itemStatParser = new ItemStatParser(
				gameVersion,
				getStatPatternRepository()::getGemStatParser,
				getItemSpellRepository()
		);
		this.itemType = ItemType.GEM;
		this.metaEnablers = new ArrayList<>();
	}

	@Override
	protected void afterParse() {
		if (color == null) {
			throw new IllegalArgumentException("Couldn't parse color for: " + name);
		}

		if (!itemStatParser.hasAnyEffects()) {
			throw new IllegalArgumentException("Couldn't parse any stat for: " + name);
		}
	}

	private boolean tryParseItemEffect(String line) {
		for (String part : getParts(line)) {
			if (!itemStatParser.tryParseItemEffect(part)) {
				return false;
			}
		}
		return true;
	}

	private static String[] getParts(String line) {
		if (line.contains(" & ")) {
			return line.split(" & ");
		} else if (line.contains(", ")) {
			return line.split(", ");
		} else if (line.contains(" and ")) {
			return line.split(" and ");
		} else {
			return new String[] {line};
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

	public List<Effect> getEffects() {
		return itemStatParser.getItemEffects();
	}
}
