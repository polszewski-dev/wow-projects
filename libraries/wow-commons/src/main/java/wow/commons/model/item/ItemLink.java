package wow.commons.model.item;

import wow.commons.model.categorization.ItemRarity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2021-01-22
 */
public record ItemLink(
		int itemId,
		String name,
		ItemRarity rarity,
		Integer enchantId,
		Integer gem1Id,
		Integer gem2Id,
		Integer gem3Id,
		Integer gem4Id,
		Integer suffixId,
		Integer uniqueId,
		Integer linkLevel,
		Integer specializationID,
		Integer upgradeId,
		Integer instanceDifficultyId,
		Integer numBonusIds,
		Integer bonusId1,
		Integer bonusId2,
		Integer upgradeValue
) {
	/*

	Item String:

	item:itemId:enchantId:gemId1:gemId2:gemId3:gemId4:suffixId:uniqueId:linkLevel:specializationID:upgradeId:instanceDifficultyId:numBonusIds:bonusId1:bonusId2:upgradeValue

	Item Link:

	|cff9d9d9d|Hitem:3299::::::::20:257::::::|h[Fractured Canine]|h|r

	 */

	private static final Pattern ITEM_LINK_REGEX = Pattern.compile("^\\|c([0-9a-f]{8})\\|H(item:.*)\\|h\\[(.*)]\\|h\\|r$");

	public ItemLink(int itemId, String name, ItemRarity rarity, Integer enchantId, Integer gem1Id, Integer gem2Id, Integer gem3Id) {
		this(itemId, name, rarity, enchantId, gem1Id, gem2Id, gem3Id, null, null, null, null, null, null, null, null, null, null, null);
	}

	public static ItemLink parse(String itemLink) {
		return parseHelper(itemLink, false);
	}

	public static ItemLink tryParse(String itemLink) {
		return parseHelper(itemLink, true);
	}

	public ItemLink setEnchantAndGems(Integer enchantId, Integer gem1Id, Integer gem2Id, Integer gem3Id, Integer gem4Id) {
		return new ItemLink(
				this.itemId,
				this.name,
				this.rarity,
				enchantId,
				gem1Id,
				gem2Id,
				gem3Id,
				gem4Id,
				this.suffixId,
				this.uniqueId,
				this.linkLevel,
				this.specializationID,
				this.upgradeId,
				this.instanceDifficultyId,
				this.numBonusIds,
				this.bonusId1,
				this.bonusId2,
				this.upgradeValue
		);
	}

	@Override
	public String toString() {
		return "|c" +
				rarity.getColor() +
				"|Hitem:" +
				itemId + ':' +
				blankIfNull(enchantId) + ':' +
				blankIfNull(gem1Id) + ':' +
				blankIfNull(gem2Id) + ':' +
				blankIfNull(gem3Id) + ':' +
				blankIfNull(gem4Id) + ':' +
				blankIfNull(suffixId) + ':' +
				blankIfNull(uniqueId) + ':' +
				blankIfNull(linkLevel) + ':' +
				blankIfNull(specializationID) + ':' +
				blankIfNull(upgradeId) + ':' +
				blankIfNull(instanceDifficultyId) + ':' +
				blankIfNull(numBonusIds) + ':' +
				blankIfNull(bonusId1) + ':' +
				blankIfNull(bonusId2) + ':' +
				blankIfNull(upgradeValue) + ':' +
				"|h[" +
				name +
				"]|h|r";
	}

	private static ItemLink parseHelper(String itemLink, boolean returnNull) {
		if (itemLink == null || itemLink.isEmpty()) {
			return null;
		}

		Matcher matcher = ITEM_LINK_REGEX.matcher(itemLink);

		if (!matcher.find()) {
			if (returnNull) {
				return null;
			}
			throw new IllegalArgumentException("This is not an item link: " + itemLink);
		}


		String name = matcher.group(3);
		ItemRarity rarity = ItemRarity.parseFromColor(matcher.group(1));

		String itemString = matcher.group(2);

		String[] itemStringParts = itemString.split(":", -1);

		int itemId = Integer.parseInt(itemStringParts[1]);

		Integer enchantId = getNullableInt(itemStringParts, 2);
		Integer gem1Id = getNullableInt(itemStringParts, 3);
		Integer gem2Id = getNullableInt(itemStringParts, 4);
		Integer gem3Id = getNullableInt(itemStringParts, 5);
		Integer gem4Id = getNullableInt(itemStringParts, 6);

		Integer suffixId = getNullableInt(itemStringParts, 7);
		Integer uniqueId = getNullableInt(itemStringParts, 8);
		Integer linkLevel = getNullableInt(itemStringParts, 9);
		Integer specializationID = getNullableInt(itemStringParts, 10);
		Integer upgradeId = getNullableInt(itemStringParts, 11);
		Integer instanceDifficultyId = getNullableInt(itemStringParts, 12);
		Integer numBonusIds = getNullableInt(itemStringParts, 13);
		Integer bonusId1 = getNullableInt(itemStringParts, 14);
		Integer bonusId2 = getNullableInt(itemStringParts, 15);
		Integer upgradeValue = getNullableInt(itemStringParts, 16);

		if (name == null || name.isEmpty()) {
			name = "#" + itemId;
		}

		return new ItemLink(itemId, name, rarity, enchantId, gem1Id, gem2Id, gem3Id, gem4Id, suffixId, uniqueId, linkLevel, specializationID, upgradeId, instanceDifficultyId, numBonusIds, bonusId1, bonusId2, upgradeValue);
	}

	private static Integer getNullableInt(String[] parts, int index) {
		if (index >= parts.length) {
			return null;
		}
		String value = parts[index];
		if (value == null || value.isEmpty() || value.equals("0")) {
			return null;
		}
		return Integer.valueOf(value);
	}

	private static String blankIfNull(Integer value) {
		return value != null ? value.toString() : "";
	}
}
