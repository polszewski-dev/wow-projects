package wow.commons.model.item;

import wow.commons.model.Money;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.pve.Phase;
import wow.commons.model.sources.Source;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public abstract class AbstractItem implements AttributeSource {
	private final int id;
	private final String name;
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

	protected AbstractItem(int itemId, String name, ItemRarity rarity, Attributes stats, Set<Source> sources) {
		this.id = itemId;
		this.name = name;
		this.rarity = rarity;
		this.stats = stats;
		this.sources = sources;
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

	public int getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}

	public ItemRestriction getRestriction() {
		return restriction;
	}

	public Money getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(Money sellPrice) {
		this.sellPrice = sellPrice;
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

	public ItemLink getItemLink() {
		return new ItemLink(id, name, rarity, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	public Phase getPhase() {
		return restriction.getPhase();
	}

	public Set<Source> getSources() {
		return sources;
	}

	public boolean isSourcedFromRaid() {
		return anySource(Source::isRaidDrop);
	}

	public boolean isSourcedFromInstance(String instanceName) {
		return anySource(source -> source.getInstance() != null && source.getInstance().getName().equalsIgnoreCase(instanceName));
	}

	public boolean isSourcedFromAnyInstance(String... instanceNames) {
		return anySource(source -> source.getInstance() != null && Arrays.asList(instanceNames).contains(source.getInstance().getName()));
	}

	public boolean isPurchasedFromBadgeVendor() {
		return anySource(Source::isBadgeVendor);
	}

	public boolean isTradedFromToken() {
		return anySource(Source::isTradedFromToken);
	}

	public boolean isCrafted() {
		return anySource(Source::isCrafted);
	}

	public boolean isPvPReward() {
		return anySource(Source::isPvp);
	}

	public boolean anySource(Predicate<Source> predicate) {
		return getSources().stream().anyMatch(predicate);
	}

	public boolean isAvailableDuring(Phase phase) {
		return getPhase().isEarlierOrTheSame(phase);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractItem)) return false;
		AbstractItem that = (AbstractItem) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public String toString() {
		return String.format("%s [%s]", name, getAttributes());
	}
}
