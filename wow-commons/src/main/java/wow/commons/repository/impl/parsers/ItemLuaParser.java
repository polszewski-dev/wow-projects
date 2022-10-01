package wow.commons.repository.impl.parsers;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.*;
import wow.commons.model.item.*;
import wow.commons.model.unit.CharacterClass;
import wow.commons.repository.ItemDataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-10
 */
public class ItemLuaParser {
	public static Item parseItem(ItemTooltip itemTooltip, ItemDataRepository itemDataRepository) {
		Item item = new Item(itemTooltip);

		if (isPattern(itemTooltip)) {
			item.setItemType(ItemType.Pattern);
		} else {
			parseItemLines(itemTooltip, item, itemDataRepository);
			item.setClassRestriction(getClassRestriction(item.getClassRestriction(), item.getItemSet()));
			fixItemType(item);
			validateItem(item);
		}

		return item;
	}

	private static boolean isPattern(ItemTooltip itemTooltip) {
		String name = itemTooltip.getItemLink().getName();

		String[] patternPrefixes = {
			"Recipe: ",
			"Plans: ",
			"Formula: ",
			"Schematic: ",
			"Pattern: ",
			"Design: ",
		};

		return Stream.of(patternPrefixes).anyMatch(name::startsWith);
	}

	private static void parseItemLines(ItemTooltip itemTooltip, Item item, ItemDataRepository itemDataRepository) {
		List<String> lines = itemTooltip.getLines();

		List<ItemSetBonus> itemSetBonuses = null;
		List<SocketType> socketTypes = null;
		Attributes socketBonus = null;

		ItemStatParser.resetAll();

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim().replace("\n", "");
			Object[] parsedValues;

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

			if (line.equals("Binds when picked up") || line.equals("Soulbound")) {
				item.setBinding(Binding.BindsOnPickUp);
				continue;
			}

			if (line.equals("Binds when equipped")) {
				item.setBinding(Binding.BindsOnEquip);
				continue;
			}

			if (line.equals("Unique") || line.startsWith("Unique-Equipped")) {
				item.setUnique(true);
				continue;
			}

			if (item.getItemType() == null) {
				item.setItemType(ItemType.tryParse(line));
				if (item.getItemType() != null) {
					continue;
				}
			}

			if (item.getItemSubType() == null) {
				item.setItemSubType(ArmorSubType.tryParse(line));
				if (item.getItemSubType() != null) {
					continue;
				}
			}

			if (item.getItemSubType() == null) {
				item.setItemSubType(WeaponSubType.tryParse(line));
				if (item.getItemSubType() != null) {
					continue;
				}
			}

			if (item.getItemSubType() == null) {
				item.setItemSubType(ProjectileSubType.tryParse(line));
				if (item.getItemSubType() != null) {
					continue;
				}
			}

			if (ItemStatParser.tryParse(line)) {
				continue;
			}

			if (line.startsWith("Classes: ")) {
				String sub = line.substring("Classes: ".length());
				item.setClassRestriction(Stream.of(sub.split(", "))
												 .map(x -> CharacterClass.parse(x.trim()))
												 .collect(Collectors.toList()));
				continue;
			}

			if (line.startsWith("Races: ")) {
				continue;
			}

			if (line.equals("Red Socket")) {
				if (socketTypes == null) {
					socketTypes = new ArrayList<>();
				}
				socketTypes.add(SocketType.Red);
				continue;
			}

			if (line.equals("Yellow Socket")) {
				if (socketTypes == null) {
					socketTypes = new ArrayList<>();
				}
				socketTypes.add(SocketType.Yellow);
				continue;
			}

			if (line.equals("Blue Socket")) {
				if (socketTypes == null) {
					socketTypes = new ArrayList<>();
				}
				socketTypes.add(SocketType.Blue);
				continue;
			}

			if (line.equals("Meta Socket")) {
				if (socketTypes == null) {
					socketTypes = new ArrayList<>();
				}
				socketTypes.add(SocketType.Meta);
				continue;
			}

			String socketBonusPrefix = "Socket Bonus: ";

			if (line.startsWith(socketBonusPrefix)) {
				socketBonus = SocketBonusParser.parseSocketBonus(line.substring(socketBonusPrefix.length()));
				continue;
			}

			parsedValues = parseMultiValue("(.*) \\(\\d/(\\d)\\)", line);
			if (parsedValues != null) {
				item.setItemSet(itemDataRepository.getItemSet((String) parsedValues[0]));
				if (item.getItemSet() == null) {
					throw new IllegalArgumentException("Unknown set: " + parsedValues[0]);
				}
				int numPieces = (int) parsedValues[1];
				if (lines.get(i + 1)
						.startsWith("Requires")) {
					++i;
				}
				i += numPieces;

				continue;
			}

			if (line.matches("\\(\\d\\) Set ?: .*")) {
				if (item.getItemSet() != null && item.getItemSet().getItemSetBonuses() == null && itemSetBonuses == null) {
					itemSetBonuses = new ArrayList<>();
				}
				if (itemSetBonuses != null) {
					itemSetBonuses.add(ItemSetBonus.parse(line));
				}
				continue;
			}

			throw new IllegalArgumentException(line);
		}

		ItemStatParser.setItemStats(item);
		ItemStatParser.checkForNotUsedMatches();

		if (socketTypes != null) {
			if (socketBonus == null) {
				throw new IllegalArgumentException();
			}
			item.setSocketSpecification(new ItemSocketSpecification(socketTypes, socketBonus));
		} else {
			if (socketBonus != null) {
				throw new IllegalArgumentException();
			}
			item.setSocketSpecification(ItemSocketSpecification.EMPTY);
		}

		if (itemSetBonuses != null) {
			item.getItemSet().setItemSetBonuses(itemSetBonuses);
		}
	}

	private static void fixItemType(Item item) {
		ItemType itemType = item.getItemType();
		ItemSubType itemSubType = item.getItemSubType();

		if (itemType == ItemType.Back && itemSubType == null) {
			item.setItemSubType(ArmorSubType.Cloth);
		}

		if (itemType != null) {
			return;
		}

		if (itemSubType == WeaponSubType.HeldInOffHand) {
			item.setItemType(ItemType.OffHand);
			return;
		}

		if (itemSubType == WeaponSubType.Wand) {
			item.setItemType(ItemType.Ranged);
			return;
		}

		if (itemSubType == WeaponSubType.Gun || itemSubType == WeaponSubType.Bow || itemSubType == WeaponSubType.Crossbow || itemSubType == WeaponSubType.Thrown) {
			item.setItemType(ItemType.Ranged);
			return;
		}

		if (List.of(
				"Fortitude of the Scourge",
				"Might of the Scourge",
				"Power of the Scourge",
				"Resilience of the Scourge").contains(item.getName())) {
			item.setItemType(ItemType.Enchant);
			return;
		}

		if (List.of(TOKEN_NAMES).contains(item.getName())) {
			item.setItemType(ItemType.Token);
			return;
		}

		throw new IllegalArgumentException("Can't determine item type for: " + item.getName());
	}

	private static final String[] TOKEN_NAMES = {
			"Helm of the Fallen Defender",
			"Pauldrons of the Fallen Defender",
			"Chestguard of the Fallen Defender",
			"Leggings of the Fallen Defender",
			"Gloves of the Fallen Defender",

			"Helm of the Fallen Champion",
			"Pauldrons of the Fallen Champion",
			"Chestguard of the Fallen Champion",
			"Gloves of the Fallen Champion",
			"Leggings of the Fallen Champion",

			"Helm of the Fallen Hero",
			"Pauldrons of the Fallen Hero",
			"Chestguard of the Fallen Hero",
			"Gloves of the Fallen Hero",
			"Leggings of the Fallen Hero",

			"Helm of the Vanquished Defender",
			"Pauldrons of the Vanquished Defender",
			"Chestguard of the Vanquished Defender",
			"Leggings of the Vanquished Defender",
			"Gloves of the Vanquished Defender",

			"Helm of the Vanquished Champion",
			"Pauldrons of the Vanquished Champion",
			"Chestguard of the Vanquished Champion",
			"Gloves of the Vanquished Champion",
			"Leggings of the Vanquished Champion",

			"Helm of the Vanquished Hero",
			"Pauldrons of the Vanquished Hero",
			"Chestguard of the Vanquished Hero",
			"Gloves of the Vanquished Hero",
			"Leggings of the Vanquished Hero",

			"Helm of the Forgotten Conqueror",
			"Pauldrons of the Forgotten Conqueror",
			"Chestguard of the Forgotten Conqueror",
			"Gloves of the Forgotten Conqueror",
			"Leggings of the Forgotten Conqueror",

			"Helm of the Forgotten Protector",
			"Pauldrons of the Forgotten Protector",
			"Chestguard of the Forgotten Protector",
			"Gloves of the Forgotten Protector",
			"Leggings of the Forgotten Protector",

			"Helm of the Forgotten Vanquisher",
			"Pauldrons of the Forgotten Vanquisher",
			"Chestguard of the Forgotten Vanquisher",
			"Gloves of the Forgotten Vanquisher",
			"Leggings of the Forgotten Vanquisher",

			"Belt of the Forgotten Conqueror",
			"Bracers of the Forgotten Conqueror",
			"Boots of the Forgotten Conqueror",

			"Belt of the Forgotten Protector",
			"Bracers of the Forgotten Protector",
			"Boots of the Forgotten Protector",

			"Belt of the Forgotten Vanquisher",
			"Bracers of the Forgotten Vanquisher",
			"Boots of the Forgotten Vanquisher",
	};

	private static void validateItem(Item item) {
		ItemType itemType = item.getItemType();
		ItemSubType itemSubType = item.getItemSubType();

		if (itemType == null) {
			return;// checked after all items are read
		}
		if (itemType.getCategory() == ItemCategory.Armor && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.Weapon && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if (itemType.getCategory() == ItemCategory.Projectile && itemSubType == null) {
			throw new IllegalArgumentException(item.getName());
		}
		if ((itemType.getCategory() != ItemCategory.Armor && itemType.getCategory() != ItemCategory.Weapon && itemType.getCategory() != ItemCategory.Projectile) && itemSubType != null) {
			throw new IllegalArgumentException(item.getName());
		}
	}

	private static Object[] parseMultiValue(String pattern, String line) {
		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(line);
		if (matcher.find()) {
			Object[] result = new Object[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				String group = matcher.group(i);
				if (group.matches("\\d+")) {
					result[i - 1] = Integer.valueOf(group);
				} else {
					result[i - 1] = group;
				}
			}
			return result;
		}
		return null;
	}

	private static List<CharacterClass> getClassRestriction(List<CharacterClass> classRestriction, ItemSet itemSet) {
		if (classRestriction != null) {
			if (itemSet != null && !itemSet.getClassRestriction()
										   .isEmpty()) {
				if (!classRestriction.equals(itemSet.getClassRestriction())) {
					throw new IllegalArgumentException();
				}
			}
			return classRestriction;
		}
		if (itemSet != null) {
			return itemSet.getClassRestriction();
		}
		return List.of();
	}
}
