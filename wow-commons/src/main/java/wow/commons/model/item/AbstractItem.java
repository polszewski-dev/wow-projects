package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.pve.Zone;
import wow.commons.model.sources.Source;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractItem extends ConfigurationElementWithAttributes<Integer> {
	private final ItemType itemType;
	private final BasicItemInfo basicItemInfo;

	protected AbstractItem(Integer id, Description description, Restriction restriction, Attributes attributes, ItemType itemType, BasicItemInfo basicItemInfo) {
		super(id, description, restriction, attributes);
		this.itemType = itemType;
		this.basicItemInfo = basicItemInfo;
	}

	public ItemLink getItemLink() {
		return new ItemLink(getId(), getName(), getRarity(), null, null, null, null);
	}

	public ItemRarity getRarity() {
		return basicItemInfo.getRarity();
	}

	public Binding getBinding() {
		return basicItemInfo.getBinding();
	}

	public boolean isUnique() {
		return basicItemInfo.isUnique();
	}

	public int getItemLevel() {
		return basicItemInfo.getItemLevel();
	}

	public Set<Source> getSources() {
		return basicItemInfo.getSources();
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

	public Set<Zone> getRaidSources() {
		return getSourcesAfterTradingTokens()
				.filter(Source::isRaidDrop)
				.map(Source::getZone)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public Set<TradedItem> getSourceItems() {
		return getSources().stream()
				.map(Source::getSourceItem)
				.collect(Collectors.toSet());
	}

	private Stream<Source> getSourcesAfterTradingTokens() {
		return getSources().stream()
				.flatMap(source -> source.isTraded() ? source.getSourceItem().getSources().stream() : Stream.of(source));
	}
}
