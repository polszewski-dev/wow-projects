package wow.commons.repository.impl.parsers.gems;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.Binding;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.ItemTooltip;
import wow.commons.model.item.MetaEnabler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-12-07
 */
public class GemLuaParser {
	public static Gem parseGem(ItemTooltip itemTooltip) {
		List<String> lines = itemTooltip.getLines();

		GemColor color = null;
		Binding binding = null;
		boolean unique = false;
		Attributes stats = null;
		List<MetaEnabler> metaEnablers = null;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();

			if (line.isEmpty()) {
				continue;
			}

			if (i == 0) {
				if (line.equals(itemTooltip.getItemLink().getName())) {
					continue;
				} else {
					throw new IllegalArgumentException("Expected item name in line #1 instead of: " + line);
				}
			}

			if (color == null && (color = tryParseColor(line)) != null) {
				continue;
			}

			if (line.equals("Binds when picked up") || line.equals("Soulbound")) {
				binding = Binding.BindsOnPickUp;
				continue;
			}

			if (line.equals("Binds when equipped")) {
				binding = Binding.BindsOnEquip;
				continue;
			}

			if (line.equals("Unique") || line.equals("Unique-Equipped")) {
				unique = true;
				continue;
			}

			if (stats == null && (stats = GemStatsParser.tryParseStats(line)) != null) {
				continue;
			}

			if (metaEnablers == null && (metaEnablers = tryParseMataEnabler(line)) != null) {
				continue;
			}

			if (line.startsWith("Requires Jewelcrafting")) {
				continue;
			}

			if (line.startsWith("|T13") || line.startsWith("|A:ParagonReputation_Bag:0:0|a")) {
				continue;
			}

			throw new IllegalArgumentException("Couldn't parse: " + line + ", gem: " + itemTooltip.getItemLink().getName());
		}

		if (color == null) {
			throw new IllegalArgumentException("Couldn't parse color for: " + itemTooltip.getItemLink().getName());
		}

		if (stats == null) {
			throw new IllegalArgumentException("Couldn't parse any stat for: " + itemTooltip.getItemLink().getName());
		}

		return new Gem(itemTooltip, color, binding, unique, stats, metaEnablers);
	}

	private static List<MetaEnabler> tryParseMataEnabler(String line) {
		List<MetaEnabler> metaEnablers = Stream.of(line.split("\n"))
										  .map(x -> stripFormatting(x.trim()))
										  .map(MetaEnabler::tryParse)
										  .collect(Collectors.toList());

		if (metaEnablers.stream().allMatch(Objects::nonNull)) {
			return metaEnablers;
		}
		return null;
	}

	private static String stripFormatting(String line) {
		String prefix = "|cff808080";
		String suffix = "|r";
		if (line.startsWith(prefix)) {
			line = line.substring(prefix.length());
		}
		if (line.endsWith(suffix)) {
			line = line.substring(0, line.length() - suffix.length());
		}
		return line;
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
