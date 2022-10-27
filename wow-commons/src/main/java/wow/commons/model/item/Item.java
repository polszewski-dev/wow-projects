package wow.commons.model.item;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Raid;
import wow.commons.model.sources.Source;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.ArmorProfficiency;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.model.unit.WeaponProfficiency;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public class Item implements AttributeSource, Sourced {
	private final ItemLink itemLink;
	private final Set<Source> sources;
	private Binding binding;
	private boolean unique;
	private ItemType itemType;
	private ItemSubType itemSubType;
	private int itemLevel;
	private final ItemRestriction restriction = new ItemRestriction();
	private ItemSet itemSet;
	private String icon;
	private String tooltip;

	private Attributes stats;
	private WeaponStats weaponStats;
	private ItemSocketSpecification socketSpecification;

	public Item(ItemLink itemLink, Set<Source> sources) {
		this.itemLink = itemLink;
		this.sources = sources;
	}

	public int getId() {
		return getItemLink().getItemId();
	}

	public String getName() {
		return itemLink.getName();
	}

	public ItemRarity getRarity() {
		return getItemLink().getRarity();
	}

	public ItemLink getItemLink() {
		return itemLink;
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
		return sources;
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

	public int getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}

	public ItemRestriction getRestriction() {
		return restriction;
	}

	@Override
	public Phase getPhase() {
		return restriction.getPhase();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
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

	public Set<Raid> getRaidSources() {
		return getSourcesAfterTradingTokens()
				.filter(Source::isRaidDrop)
				.map(source -> (Raid)source.getInstance())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<Item> getSourceTokens() {
		return getSources().stream()
				.map(Source::getSourceToken)
				.collect(Collectors.toSet());
	}

	private Stream<Source> getSourcesAfterTradingTokens() {
		return getSources().stream()
				.flatMap(source -> source.isTradedFromToken() ? source.getSourceToken().getSources().stream() : Stream.of(source));
	}

	public boolean canBeEquippedBy(CharacterInfo characterInfo, Phase phase) {
		ItemCategory category = itemType.getCategory();
		if (!(category == ItemCategory.Armor || category == ItemCategory.Accessory || category == ItemCategory.Weapon)) {
			return false;
		}
		if (itemType.getCategory() == ItemCategory.Armor && !ArmorProfficiency.matches(characterInfo.getCharacterClass(), (ArmorSubType)itemSubType)) {
			return false;
		}
		if (itemType.getCategory() == ItemCategory.Weapon && !WeaponProfficiency.matches(characterInfo.getCharacterClass(), itemType, (WeaponSubType)itemSubType)) {
			return false;
		}
		if (!restriction.isMetBy(characterInfo, phase)) {
			return false;
		}
		if (itemSet != null && !itemSet.canBeEquippedBy(characterInfo, phase)) {
			return false;
		}
		return true;
	}

	public boolean isCasterItem(SpellSchool spellSchool) {
		if (hasCasterStats(spellSchool)) {
			return true;
		}
		if (HARDCODED_CASTER_ITEM_NAMES.contains(getName())) {
			return true;
		}
		if (itemType == ItemType.Trinket) {
			return getSpecialAbilities()
					.stream()
					.anyMatch(x -> x.getLine().contains("spell"));
		}
		return false;
	}

	private static final List<String> HARDCODED_CASTER_ITEM_NAMES = List.of("Shroud of the Highborne");

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
