package wow.scraper.parsers;

import lombok.Getter;
import wow.commons.model.Money;
import wow.commons.model.categorization.Binding;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.professions.Profession;
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
	private Profession requiredProfession;
	private Integer requiredProfessionLevel;
	private Money sellPrice;

	public GemTooltipParser(Integer itemId, String htmlTooltip) {
		super(itemId, htmlTooltip);
	}

	@Override
	protected Rule[] getRules() {
		return new Rule[] {
				Rule.prefix("Phase ", x -> this.phase = parsePhase(x)),
				Rule.prefix("Item Level ", x -> this.itemLevel = parseItemLevel(x)),
				Rule.tryParse(GemTooltipParser::tryParseColor, x -> this.color = x),
				Rule.exact("Binds when picked up", () -> this.binding = Binding.BindsOnPickUp),
				Rule.exact("Binds when equipped", () -> this.binding = Binding.BindsOnEquip),
				Rule.exact("Unique", () -> this.unique = true),
				Rule.exact("Unique-Equipped", () -> this.unique = true),
				Rule.tryParse(MetaEnabler::tryParse, x -> metaEnablers.add(x)),
				Rule.regex("Requires (Alchemy|Engineering|Jewelcrafting|Tailoring) \\((\\d+)\\)", this::parseRequiredProfession),
				Rule.prefix("Sell Price: ", x -> this.sellPrice = parseSellPrice(x)),
				Rule.testNotNull(GemStatsParser::tryParseStats, x -> statLines.add(x)),
		};
	};

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

	private void parseRequiredProfession(Object[] params) {
		this.requiredProfession = Profession.tryParse((String)params[0]);
		this.requiredProfessionLevel = (Integer)params[1];
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
