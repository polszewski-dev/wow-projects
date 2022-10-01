package wow.commons.model.item;

import wow.commons.model.categorization.ItemRarity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2021-01-22
 */
public class ItemLink implements Comparable<ItemLink> {
	private static final Pattern ITEM_LINK_REGEX = Pattern.compile("Hitem:(\\d+):(\\d*):(\\d*):(\\d*):(\\d*):.*\\|h\\[(.*)]\\|h\\|r");

	private final int id;
	private final String name;
	private final String link;
	private final ItemRarity rarity;
	private final Integer enchantId;
	private final Integer gem1Id;
	private final Integer gem2Id;
	private final Integer gem3Id;

	private ItemLink(int id, String name, String link, Integer enchantId, Integer gem1Id, Integer gem2Id, Integer gem3Id) {
		this.id = id;
		this.name = name;
		this.link = link;
		this.rarity = ItemRarity.parseFromItemLink(link);
		this.enchantId = enchantId;
		this.gem1Id = gem1Id;
		this.gem2Id = gem2Id;
		this.gem3Id = gem3Id;
	}

	public static ItemLink parse(String itemLink) {
		return parseHelper(itemLink, false);
	}

	public static ItemLink tryParse(String itemLink) {
		return parseHelper(itemLink, true);
	}

	public ItemLink setEnchantAndGems(Integer enchantId, Integer gem1Id, Integer gem2Id, Integer gem3Id) {
		Matcher matcher = ITEM_LINK_REGEX.matcher(this.link);

		if (!matcher.find()) {
			throw new IllegalStateException("Should never happen");
		}

		String newLink = this.link.substring(0, matcher.start(2)) +
				(enchantId != null ? enchantId : "") +
				this.link.substring(matcher.end(2), matcher.start(3)) +
				(gem1Id != null ? gem1Id : "") +
				this.link.substring(matcher.end(3), matcher.start(4)) +
				(gem2Id != null ? gem2Id : "") +
				this.link.substring(matcher.end(4), matcher.start(5)) +
				(gem3Id != null ? gem3Id : "") +
				this.link.substring(matcher.end(5));

		return parse(newLink);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ItemRarity getRarity() {
		return rarity;
	}

	public Integer getEnchantId() {
		return enchantId;
	}

	public Integer getGem1Id() {
		return gem1Id;
	}

	public Integer getGem2Id() {
		return gem2Id;
	}

	public Integer getGem3Id() {
		return gem3Id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ItemLink itemLink = (ItemLink) o;
		return Objects.equals(link, itemLink.link);
	}

	@Override
	public int hashCode() {
		return Objects.hash(link);
	}

	@Override
	public int compareTo(ItemLink o) {
		return this.link.compareTo(o.link);
	}

	@Override
	public String toString() {
		return link;
	}

	private static ItemLink parseHelper(String itemLink, boolean returnNull) {
		if (itemLink == null || itemLink.isEmpty()) {
			return null;
		}

		Matcher idMatcher = ITEM_LINK_REGEX.matcher(itemLink);

		if (!idMatcher.find()) {
			if (returnNull) {
				return null;
			}
			throw new IllegalArgumentException("This is not an item link: " + itemLink);
		}

		int id = Integer.parseInt(idMatcher.group(1));
		String name = idMatcher.group(6);

		if (name == null || name.isEmpty()) {
			name = "#" + id;
		}

		Integer enchantId = getNullableInt(idMatcher.group(2));
		Integer gem1Id = getNullableInt(idMatcher.group(3));
		Integer gem2Id = getNullableInt(idMatcher.group(4));
		Integer gem3Id = getNullableInt(idMatcher.group(5));

		return new ItemLink(id, name, itemLink, enchantId, gem1Id, gem2Id, gem3Id);
	}

	private static Integer getNullableInt(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Integer.valueOf(value);
	}
}
