package wow.commons.model.item;

import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterInfo;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-23
 */
public class ItemSet {
	private final String name;
	private final Tier tier;
	private final ItemRestriction restriction = new ItemRestriction();
	private final List<ItemSetBonus> itemSetBonuses;
	private final List<Item> pieces;

	public ItemSet(String name, Tier tier, List<ItemSetBonus> itemSetBonuses, List<Item> pieces) {
		this.name = name;
		this.tier = tier;
		this.itemSetBonuses = itemSetBonuses;
		this.pieces = pieces;
	}

	public String getName() {
		return name;
	}

	public Tier getTier() {
		return tier;
	}

	public ItemRestriction getRestriction() {
		return restriction;
	}

	public List<ItemSetBonus> getItemSetBonuses() {
		return itemSetBonuses;
	}

	public List<Item> getPieces() {
		return pieces;
	}

	public boolean canBeEquippedBy(CharacterInfo characterInfo, Phase phase) {
		return restriction.isMetBy(characterInfo, phase);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemSet)) return false;
		ItemSet itemSet = (ItemSet) o;
		return name.equals(itemSet.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
