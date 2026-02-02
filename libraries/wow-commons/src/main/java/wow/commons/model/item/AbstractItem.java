package wow.commons.model.item;

import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.pve.PhaseId;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public interface AbstractItem<T extends AbstractItemId> extends Described, TimeRestricted, CharacterRestricted {
	T getId();

	BasicItemInfo getBasicItemInfo();

	PhaseId getFirstAppearedInPhase();

	default ItemLink getItemLink() {
		return new ItemLink(getId().value(), getName(), getRarity(), null, null, null, null);
	}

	default ItemType getItemType() {
		return getBasicItemInfo().itemType();
	}

	default ItemSubType getItemSubType() {
		return getBasicItemInfo().itemSubType();
	}

	default ItemRarity getRarity() {
		return getBasicItemInfo().rarity();
	}

	default Binding getBinding() {
		return getBasicItemInfo().binding();
	}

	default boolean isUnique() {
		return getBasicItemInfo().unique();
	}

	default boolean isEffectivelyUnique() {
		return isUnique() || isAvailableOnlyByQuests();
	}

	default int getItemLevel() {
		return getBasicItemInfo().itemLevel();
	}

	default Set<ItemSource> getSources() {
		return getBasicItemInfo().sources();
	}

	default boolean isSourcedFromRaid() {
		return anySource(ItemSource::isRaidDrop);
	}

	default boolean isSourcedFromInstance(String instanceName) {
		return anySource(source -> source.zones().stream().anyMatch(x -> x.getName().equalsIgnoreCase(instanceName)));
	}

	default boolean isSourcedFromAnyInstance(String... instanceNames) {
		return Arrays.stream(instanceNames).anyMatch(this::isSourcedFromInstance);
	}

	default boolean isPurchasedFromBadgeVendor() {
		return anySource(ItemSource::isBadgeVendor);
	}

	default boolean isTradedFromToken() {
		return anySource(ItemSource::isTraded);
	}

	default boolean isCrafted() {
		return anySource(ItemSource::isCrafted);
	}

	default boolean isPvPReward() {
		return anySource(ItemSource::isPvP);
	}

	default boolean isQuestReward() {
		return anySource(ItemSource::isQuestReward);
	}

	default boolean isAvailableOnlyByQuests() {
		return allSources(ItemSource::isQuestReward);
	}

	default boolean anySource(Predicate<ItemSource> predicate) {
		return getSources().stream().anyMatch(predicate);
	}

	default boolean allSources(Predicate<ItemSource> predicate) {
		return !getSources().isEmpty() && getSources().stream().allMatch(predicate);
	}
}
