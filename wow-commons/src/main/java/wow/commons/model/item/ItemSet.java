package wow.commons.model.item;

import wow.commons.model.unit.CharacterClass;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-23
 */
public class ItemSet {
	private final String name;
	private final Tier tier;
	private final List<CharacterClass> classRestriction;

	private List<ItemSetBonus> itemSetBonuses;

	public ItemSet(String name, Tier tier, List<CharacterClass> classRestriction) {
		this.name = name;
		this.tier = tier;
		this.classRestriction = classRestriction;
	}

	public String getName() {
		return name;
	}

	public Tier getTier() {
		return tier;
	}

	public List<CharacterClass> getClassRestriction() {
		return classRestriction;
	}

	public List<ItemSetBonus> getItemSetBonuses() {
		return itemSetBonuses;
	}

	public void setItemSetBonuses(List<ItemSetBonus> itemSetBonuses) {
		this.itemSetBonuses = itemSetBonuses;
	}

	public boolean canBeEquippedBy(CharacterClass characterClass) {
		return classRestriction.isEmpty() || classRestriction.contains(characterClass);
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
