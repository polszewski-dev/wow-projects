package wow.minmax.service.impl.enumerator;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.Effect;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-23
 */
public abstract class FilterOutWorseChoices<T> {
	private final List<T> nonModifiers;
	private final List<T> modifiers;

	protected FilterOutWorseChoices(List<T> choices) {
		var isModifier = choices.stream().collect(Collectors.partitioningBy(
				x -> getEffects(x).stream().allMatch(Effect::hasModifierComponentOnly)
		));
		this.nonModifiers = isModifier.get(false);
		this.modifiers = isModifier.get(true);
	}

	public List<T> getResult() {
		var groups = modifiers.stream()
				.collect(Collectors.groupingBy(this::getGroupKey))
				.values();

		var filteredModifiers = groups.stream()
				.map(this::filterOutWorseChoicesFromGroup)
				.map(this::removeTheSameStats)
				.flatMap(Collection::stream);

		return Stream.concat(nonModifiers.stream(), filteredModifiers).toList();
	}

	private List<T> filterOutWorseChoicesFromGroup(List<T> group) {
		var keys = getAttributeKeys(group);
		return group.stream()
				.filter(choice -> !isStrictlyWorseThanOthers(choice, group, keys))
				.toList();
	}

	private boolean isStrictlyWorseThanOthers(T choice, List<T> choices, List<Attribute> keys) {
		return choices.stream()
				.filter(otherChoice -> choice != otherChoice)
				.anyMatch(otherChoice -> isStrictlyWorseThan(choice, otherChoice, keys));
	}

	private boolean isStrictlyWorseThan(T left, T right, List<Attribute> keys) {
		int ltCnt = 0;

		for (var key : keys) {
			var leftValue = sum(left, key);
			var rightValue = sum(right, key);

			if (leftValue < rightValue) {
				++ltCnt;
			} else if (leftValue > rightValue) {
				return false;
			}
		}

		return ltCnt > 0;
	}

	private double sum(T choice, Attribute key) {
		return getAttributes(choice).stream()
				.filter(x -> x.id() == key.id() && x.condition().equals(key.condition()) && x.levelScaled() == key.levelScaled())
				.mapToDouble(Attribute::value)
				.sum();
	}

	protected abstract List<Effect> getEffects(T choice);

	private List<Attribute> getAttributes(T choice) {
		return getEffects(choice).stream()
				.flatMap(x -> x.getModifierAttributeList().stream())
				.toList();
	}

	private List<T> removeTheSameStats(List<T> group) {
		return group.stream()
				.collect(Collectors.groupingBy(this::getStatsKey))
				.values().stream()
				.map(this::removeBasedOnSource)
				.toList();
	}

	private T removeBasedOnSource(List<T> choices) {
		return choices.stream()
				.min(Comparator.comparingInt(this::getOrderWithinTheSameStatChoices))
				.orElseThrow();
	}

	protected abstract int getOrderWithinTheSameStatChoices(T choice);

	protected abstract Object getGroupKey(T choice);

	private String getStatsKey(T choice) {
		return getAttributes(choice).stream()
				.map(Objects::toString)
				.distinct()
				.sorted()
				.collect(Collectors.joining("#"));
	}

	private List<Attribute> getAttributeKeys(List<T> group) {
		return group.stream()
				.map(this::getAttributes)
				.flatMap(Collection::stream)
				.map(x -> Attribute.of(x.id(), 1, x.condition(), x.levelScaled()))
				.distinct()
				.toList();
	}
}
