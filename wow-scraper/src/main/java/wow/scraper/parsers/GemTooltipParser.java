package wow.scraper.parsers;

import lombok.Getter;
import wow.commons.model.Money;
import wow.commons.model.categorization.Binding;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
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
	private Phase phase;
	private Integer itemLevel;
	private GemColor color;
	private Binding binding;
	private boolean unique;
	private List<MetaEnabler> metaEnablers;
	private List<String> statLines;
	private Money sellPrice;

	public GemTooltipParser(Integer itemId, String htmlTooltip) {
		super(itemId, htmlTooltip);
	}

	@Override
	protected void parseLine(String currentLine) {
		if (currentLine.startsWith("Phase ")) {
			this.phase = parsePhase(currentLine);
			return;
		}

		if (currentLine.startsWith("Item Level ")) {
			this.itemLevel = parseItemLevel(currentLine);
			return;
		}

		if (color == null && (color = tryParseColor(currentLine)) != null) {
			return;
		}

		if (currentLine.equals("Binds when picked up")) {
			this.binding = Binding.BindsOnPickUp;
			return;
		}

		if (currentLine.equals("Binds when equipped")) {
			this.binding = Binding.BindsOnEquip;
			return;
		}

		if (currentLine.equals("Unique") || currentLine.equals("Unique-Equipped")) {
			this.unique = true;
			return;
		}

		MetaEnabler metaEnabler = MetaEnabler.tryParse(currentLine);
		if (metaEnabler != null) {
			metaEnablers.add(metaEnabler);
			return;
		}

		if (currentLine.startsWith("Requires Jewelcrafting")) {
			//TODO
			return;
		}

		if (currentLine.startsWith("Sell Price: ")) {
			this.sellPrice = parseSellPrice(currentLine);
			return;
		}

		if (GemStatsParser.tryParseStats(currentLine) != null) {
			statLines.add(currentLine);
			return;
		}

		unmatchedLine(currentLine);
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

		if (statLines.isEmpty()) {
			throw new IllegalArgumentException("Couldn't parse any stat for: " + name);
		}
	}

	private static GemColor tryParseColor(String line) {
		if (line.equals("\"Matches a Red Socket.\"") || line.equals("\"Matches a Red Socket.  Socketing this gem causes the item to become Soulbound.\"")) {
			return GemColor.Red;
		}
		if (line.equals("\"Matches a Yellow Socket.\"") || line.equals("\"Matches a Yellow Socket.  Socketing this gem causes the item to become Soulbound.\"")) {
			return GemColor.Yellow;
		}
		if (line.equals("\"Matches a Blue Socket.\"") || line.startsWith("\"Matches a Blue Socket.  Socketing this gem causes the item to become Soulbound.\"")) {
			return GemColor.Blue;
		}
		if (line.equals("\"Matches a Red or Yellow Socket.\"") || line.equals("\"Matches a Yellow or Red Socket.\"")) {
			return GemColor.Orange;
		}
		if (line.equals("\"Matches a Red or Blue Socket.\"")) {
			return GemColor.Purple;
		}
		if (line.equals("\"Matches a Yellow or Blue Socket.\"") || line.equals("\"Matches a Blue or Yellow Socket.\"")) {
			return GemColor.Green;
		}
		if (line.equals("\"Matches a Red, Yellow or Blue Socket.\"")) {
			return GemColor.Prismatic;
		}
		if (line.equals("\"Only fits in a meta gem slot.\"")) {
			return GemColor.Meta;
		}
		return null;
	}
}
