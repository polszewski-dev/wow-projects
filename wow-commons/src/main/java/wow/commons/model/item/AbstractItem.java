package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.Money;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.pve.Phase;
import wow.commons.model.sources.Source;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractItem implements AttributeSource {
	private final int id;
	private final String name;
	private final ItemType itemType;
	private final ItemRarity rarity;
	private Binding binding;
	private boolean unique;
	private int itemLevel;
	private final ItemRestriction restriction = new ItemRestriction();
	private Money sellPrice;
	private String icon;
	private String tooltip;

	private final Attributes stats;
	private final Set<Source> sources;

	protected AbstractItem(int itemId, String name, ItemType itemType, ItemRarity rarity, Attributes stats, Set<Source> sources) {
		this.id = itemId;
		this.name = name;
		this.itemType = itemType;
		this.rarity = rarity;
		this.stats = stats;
		this.sources = sources;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}

	public void setSellPrice(Money sellPrice) {
		this.sellPrice = sellPrice;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public Attributes getAttributes() {
		return stats;
	}

	public ItemLink getItemLink() {
		return new ItemLink(id, name, rarity, null, null, null, null);
	}

	public Phase getPhase() {
		return restriction.getPhase();
	}

	public boolean isSourcedFromRaid() {
		return anySource(Source::isRaidDrop);
	}

	public boolean isSourcedFromInstance(String instanceName) {
		return anySource(source -> source.getZone() != null && source.getZone().getName().equalsIgnoreCase(instanceName));
	}

	public boolean isSourcedFromAnyInstance(String... instanceNames) {
		return anySource(source -> source.getZone() != null && Arrays.asList(instanceNames).contains(source.getZone().getName()));
	}

	public boolean isPurchasedFromBadgeVendor() {
		return anySource(Source::isBadgeVendor);
	}

	public boolean isTradedFromToken() {
		return anySource(Source::isTraded);
	}

	public boolean isCrafted() {
		return anySource(Source::isCrafted);
	}

	public boolean isPvPReward() {
		return anySource(Source::isPvP);
	}

	public boolean isQuestReward() {
		return anySource(Source::isQuestReward);
	}

	public boolean isAvailableOnlyByQuests() {
		return allSources(Source::isQuestReward);
	}

	public boolean anySource(Predicate<Source> predicate) {
		return getSources().stream().anyMatch(predicate);
	}

	public boolean allSources(Predicate<Source> predicate) {
		return getSources().stream().allMatch(predicate);
	}

	public boolean isAvailableDuring(Phase phase) {
		return getPhase().isEarlierOrTheSame(phase);
	}

	public abstract boolean equals(Object o);

	public abstract int hashCode();

	public String toString() {
		return String.format("%s [%s]", name, getAttributes());
	}
}
