package wow.commons.model.item;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.pve.Zone;
import wow.commons.model.sources.Source;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public interface AbstractItem extends ConfigurationElementWithAttributes<Integer> {
	BasicItemInfo getBasicItemInfo();

	default ItemLink getItemLink() {
		return new ItemLink(getId(), getName(), getRarity(), null, null, null, null);
	}

	default ItemType getItemType() {
		return getBasicItemInfo().getItemType();
	}

	default ItemSubType getItemSubType() {
		return getBasicItemInfo().getItemSubType();
	}

	default ItemRarity getRarity() {
		return getBasicItemInfo().getRarity();
	}

	default Binding getBinding() {
		return getBasicItemInfo().getBinding();
	}

	default boolean isUnique() {
		return getBasicItemInfo().isUnique();
	}

	default int getItemLevel() {
		return getBasicItemInfo().getItemLevel();
	}

	default Set<Source> getSources() {
		return getBasicItemInfo().getSources();
	}

	default boolean isSourcedFromRaid() {
		return anySource(Source::isRaidDrop);
	}

	default boolean isSourcedFromInstance(String instanceName) {
		return anySource(source -> source.getZones().stream().anyMatch(x -> x.getName().equalsIgnoreCase(instanceName)));
	}

	default boolean isSourcedFromAnyInstance(String... instanceNames) {
		return Arrays.stream(instanceNames).anyMatch(this::isSourcedFromInstance);
	}

	default boolean isPurchasedFromBadgeVendor() {
		return anySource(Source::isBadgeVendor);
	}

	default boolean isTradedFromToken() {
		return anySource(Source::isTraded);
	}

	default boolean isCrafted() {
		return anySource(Source::isCrafted);
	}

	default boolean isPvPReward() {
		return anySource(Source::isPvP);
	}

	default boolean isQuestReward() {
		return anySource(Source::isQuestReward);
	}

	default boolean isAvailableOnlyByQuests() {
		return allSources(Source::isQuestReward);
	}

	default boolean anySource(Predicate<Source> predicate) {
		return getSources().stream().anyMatch(predicate);
	}

	default boolean allSources(Predicate<Source> predicate) {
		return getSources().stream().allMatch(predicate);
	}

	default Set<Zone> getRaidSources() {
		return getSourcesAfterTradingTokens()
				.filter(Source::isRaidDrop)
				.map(Source::getZones)
				.flatMap(Collection::stream)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	default Set<TradedItem> getSourceItems() {
		return getSources().stream()
				.map(Source::getSourceItem)
				.collect(Collectors.toSet());
	}

	default Stream<Source> getSourcesAfterTradingTokens() {
		return getSources().stream()
				.flatMap(source -> source.isTraded() ? source.getSourceItem().getSources().stream() : Stream.of(source));
	}
}
