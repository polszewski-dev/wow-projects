package wow.commons.model.item;

import wow.commons.model.pve.Phase;
import wow.commons.model.sources.Source;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2021-12-08
 */
public interface Sourced {
	Set<Source> getSources();

	default boolean isSourcedFromRaid() {
		return anySource(Source::isRaidDrop);
	}

	default boolean isSourcedFromInstance(String instanceName) {
		return anySource(source -> source.getInstance() != null && source.getInstance().getName().equalsIgnoreCase(instanceName));
	}

	default boolean isSourcedFromAnyInstance(String... instanceNames) {
		return anySource(source -> source.getInstance() != null && Arrays.asList(instanceNames).contains(source.getInstance().getName()));
	}

	default boolean isPurchasedFromBadgeVendor() {
		return anySource(Source::isBadgeVendor);
	}

	default boolean isTradedFromToken() {
		return anySource(Source::isTradedFromToken);
	}

	default boolean isCrafted() {
		return anySource(Source::isCrafted);
	}

	default boolean isPvPReward() {
		return anySource(Source::isPvp);
	}

	default boolean anySource(Predicate<Source> predicate) {
		return getSources().stream().anyMatch(predicate);
	}

	Phase getPhase();

	default boolean isAvailableDuring(Phase phase) {
		return getPhase().isEarlierOrTheSame(phase);
	}
}
