package wow.commons.model.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.categorization.ItemRarity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2021-01-22
 */
@Getter
@EqualsAndHashCode
public class ItemLink {
	/*

	Item String:

	item:itemId:enchantId:gemId1:gemId2:gemId3:gemId4:suffixId:uniqueId:linkLevel:specializationID:upgradeId:instanceDifficultyId:numBonusIds:bonusId1:bonusId2:upgradeValue

	Item Link:

	|cff9d9d9d|Hitem:3299::::::::20:257::::::|h[Fractured Canine]|h|r

	 */

	private static final Pattern ITEM_LINK_REGEX = Pattern.compile("^\\|c([0-9a-f]{8})\\|H(item:.*)\\|h\\[(.*)]\\|h\\|r$");

	private final int itemId;
	private final String name;
	private final ItemRarity rarity;

	private final Integer enchantId;
	private final Integer gem1Id;
	private final Integer gem2Id;
	private final Integer gem3Id;
	private final Integer gem4Id;

	private final Integer suffixId;
	private final Integer uniqueId;
	private final Integer linkLevel;
	private final Integer specializationID;
	private final Integer upgradeId;
	private final Integer instanceDifficultyId;
	private final Integer numBonusIds;
	private final Integer bonusId1;
	private final Integer bonusId2;
	private final Integer upgradeValue;

	public ItemLink(int itemId, String name, ItemRarity rarity, Integer enchantId, Integer gem1Id, Integer gem2Id, Integer gem3Id, Integer gem4Id, Integer suffixId, Integer uniqueId, Integer linkLevel, Integer specializationID, Integer upgradeId, Integer instanceDifficultyId, Integer numBonusIds, Integer bonusId1, Integer bonusId2, Integer upgradeValue) {
		this.itemId = itemId;
		this.name = name;
		this.rarity = rarity;
		this.enchantId = enchantId;
		this.gem1Id = gem1Id;
		this.gem2Id = gem2Id;
		this.gem3Id = gem3Id;
		this.gem4Id = gem4Id;
		this.suffixId = suffixId;
		this.uniqueId = uniqueId;
		this.linkLevel = linkLevel;
		this.specializationID = specializationID;
		this.upgradeId = upgradeId;
		this.instanceDifficultyId = instanceDifficultyId;
		this.numBonusIds = numBonusIds;
		this.bonusId1 = bonusId1;
		this.bonusId2 = bonusId2;
		this.upgradeValue = upgradeValue;
	}

	public static ItemLink parse(String itemLink) {
		return parseHelper(itemLink, false);
	}

	public static ItemLink tryParse(String itemLink) {
		return parseHelper(itemLink, true);
	}

	public ItemLink setEnchantAndGems(Integer enchantId, Integer gem1Id, Integer gem2Id, Integer gem3Id) {
		return new ItemLink(
				this.itemId,
				this.name,
				this.rarity,
				enchantId,
				gem1Id,
				gem2Id,
				gem3Id,
				this.gem4Id,
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
				"]|h|r"
		;
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
