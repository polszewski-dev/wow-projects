package wow.commons.model.item;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.*;
import wow.commons.model.professions.Profession;
import wow.commons.model.sources.Source;
import wow.commons.model.unit.ArmorProfficiency;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.WeaponProfficiency;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public class Item implements AttributeSource, Sourced {
	private final ItemTooltip tooltip;
	private Binding binding;
	private boolean unique;
	private ItemType itemType;
	private ItemSubType itemSubType;
	private ItemSet itemSet;
	private List<CharacterClass> classRestriction;
	private Profession professionRestriction;
	private int requiredLevel;
	private int itemLevel;

	private Attributes stats;
	private WeaponStats weaponStats;
	private ItemSocketSpecification socketSpecification;

	public Item(ItemTooltip tooltip) {
		this.tooltip = tooltip;
	}

	public int getId() {
		return getItemLink().getId();
	}

	public String getName() {
		return tooltip.getItemLink().getName();
	}

	public ItemRarity getRarity() {
		return getItemLink().getRarity();
	}

	public ItemLink getItemLink() {
		return tooltip.getItemLink();
	}

	public boolean canBeEquippedBy(CharacterClass characterClass) {
		if (classRestriction ==  null) {
			return true;
		}
		if (!classRestriction.isEmpty() && !classRestriction.contains(characterClass)) {
			return false;
		}
		if (itemSet != null && !itemSet.canBeEquippedBy(characterClass)) {
			return false;
		}
		if (itemType.getCategory() == ItemCategory.Armor && !ArmorProfficiency.matches(characterClass, (ArmorSubType)itemSubType)) {
			return false;
		}
		if (itemType.getCategory() == ItemCategory.Weapon && !WeaponProfficiency.matches(characterClass, itemType, (WeaponSubType)itemSubType)) {
			return false;
		}
		return true;
	}

	public boolean isEnchantable() {
		return itemType.isEnchantable(itemSubType);
	}

	public int getSocketCount() {
		return socketSpecification.getSocketCount();
	}

	public boolean hasSockets() {
		return socketSpecification.hasSockets();
	}

	public SocketType getSocketType(int i) {
		return socketSpecification.getSocketType(i);
	}

	public Attributes getSocketBonus() {
		return socketSpecification.getSocketBonus();
	}

	@Override
	public Set<Source> getSources() {
		return tooltip.getSources();
	}

	public ItemTooltip getTooltip() {
		return tooltip;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public ItemSubType getItemSubType() {
		return itemSubType;
	}

	public void setItemSubType(ItemSubType itemSubType) {
		this.itemSubType = itemSubType;
	}

	public boolean canBeEquippedIn(ItemSlot itemSlot) {
		return itemType.getItemSlots().contains(itemSlot);
	}

	public ItemSet getItemSet() {
		return itemSet;
	}

	public void setItemSet(ItemSet itemSet) {
		this.itemSet = itemSet;
	}

	public List<CharacterClass> getClassRestriction() {
		return classRestriction;
	}

	public void setClassRestriction(List<CharacterClass> classRestriction) {
		this.classRestriction = classRestriction;
	}

	public Profession getProfessionRestriction() {
		return professionRestriction;
	}

	public void setProfessionRestriction(Profession professionRestriction) {
		this.professionRestriction = professionRestriction;
	}

	public int getRequiredLevel() {
		return requiredLevel;
	}

	public void setRequiredLevel(int requiredLevel) {
		this.requiredLevel = requiredLevel;
	}

	public int getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}

	@Override
	public Attributes getAttributes() {
		return stats;
	}

	public WeaponStats getWeaponStats() {
		return weaponStats;
	}

	public void setWeaponStats(WeaponStats weaponStats) {
		this.weaponStats = weaponStats;
	}

	public ItemSocketSpecification getSocketSpecification() {
		return socketSpecification;
	}

	public void setSocketSpecification(ItemSocketSpecification socketSpecification) {
		this.socketSpecification = socketSpecification;
	}

	public void setStats(Attributes stats) {
		this.stats = stats;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Item item = (Item) o;
		return Objects.equals(getItemLink(), item.getItemLink());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getItemLink());
	}

	@Override
	public String toString() {
		return getName();
	}
}