package wow.commons.model.item;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Zone;
import wow.commons.model.sources.Source;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.ArmorProfficiency;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.model.unit.WeaponProfficiency;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public class Item extends AbstractItem {
	private final ItemType itemType;
	private final ItemSubType itemSubType;
	private ItemSet itemSet;
	private final ItemSocketSpecification socketSpecification;
	private final WeaponStats weaponStats;

	public Item(int itemId,
				String name,
				ItemRarity rarity,
				ItemType itemType,
				ItemSubType itemSubType,
				Set<Source> sources,
				ItemSocketSpecification socketSpecification,
				Attributes stats,
				WeaponStats weaponStats) {
		super(itemId, name, rarity, stats, sources);
		this.itemType = itemType;
		this.itemSubType = itemSubType;
		this.socketSpecification = socketSpecification;
		this.weaponStats = weaponStats;
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

	public ItemType getItemType() {
		return itemType;
	}

	public ItemSubType getItemSubType() {
		return itemSubType;
	}

	public ItemSet getItemSet() {
		return itemSet;
	}

	public void setItemSet(ItemSet itemSet) {
		this.itemSet = itemSet;
	}

	public boolean canBeEquippedIn(ItemSlot itemSlot) {
		return itemType.getItemSlots().contains(itemSlot);
	}

	public WeaponStats getWeaponStats() {
		return weaponStats;
	}

	public ItemSocketSpecification getSocketSpecification() {
		return socketSpecification;
	}

	public Set<Zone> getRaidSources() {
		return getSourcesAfterTradingTokens()
				.filter(Source::isRaidDrop)
				.map(Source::getInstance)
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
		if (!(category == ItemCategory.ARMOR || category == ItemCategory.ACCESSORY || category == ItemCategory.WEAPON)) {
			return false;
		}
		if (itemType.getCategory() == ItemCategory.ARMOR && !ArmorProfficiency.matches(characterInfo.getCharacterClass(), (ArmorSubType)itemSubType)) {
			return false;
		}
		if (itemType.getCategory() == ItemCategory.WEAPON && !WeaponProfficiency.matches(characterInfo.getCharacterClass(), itemType, (WeaponSubType)itemSubType)) {
			return false;
		}
		if (!getRestriction().isMetBy(characterInfo, phase)) {
			return false;
		}
		return itemSet == null || itemSet.canBeEquippedBy(characterInfo, phase);
	}

	public boolean isCasterItem(SpellSchool spellSchool) {
		if (hasCasterStats(spellSchool)) {
			return true;
		}
		if (HARDCODED_CASTER_ITEM_NAMES.contains(getName())) {
			return true;
		}
		if (itemType == ItemType.TRINKET) {
			return getSpecialAbilities()
					.stream()
					.anyMatch(x -> x.getLine().contains("spell"));
		}
		return false;
	}

	private static final List<String> HARDCODED_CASTER_ITEM_NAMES = List.of("Shroud of the Highborne");
}
