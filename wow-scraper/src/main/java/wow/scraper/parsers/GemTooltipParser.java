package wow.scraper.parsers;

import lombok.Getter;
import wow.commons.model.categorization.Binding;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.repository.impl.parsers.gems.GemStatsParser;

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
	private List<String> statLines;

	public GemTooltipParser(Integer itemId, String htmlTooltip, GameVersion gameVersion) {
		super(itemId, htmlTooltip, gameVersion);
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
				Rule.testNotNull(GemStatsParser::tryParseStats, x -> statLines.add(x)),
		};
	}

	@Override
	protected void beforeParse() {
		this.metaEnablers = new ArrayList<>();
		this.statLines = new ArrayList<>();
	}

	@Override
	protected void afterParse() {
		if (color == null) {
			throw new IllegalArgumentException("Couldn't parse color for: " + name);
		}

		if (binding == null) {
			this.binding = Binding.BINDS_ON_EQUIP;
		}

		if (phase == null) {
			this.phase = Phase.TBC_P1;
		}

		if (statLines.isEmpty()) {
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
