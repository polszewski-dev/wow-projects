package wow.character.service.impl.enumerator;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.AttributeCondition;
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
		var filteredModifiers = modifiers.stream()
				.collect(Collectors.groupingBy(this::getGroupAndStatsKey))
				.values().stream()
				.map(this::filterOutWorseChoicesFromGroup)
				.map(this::removeTheSameStats)
				.flatMap(Collection::stream);

		return Stream.concat(nonModifiers.stream(), filteredModifiers).toList();
	}

	private List<T> filterOutWorseChoicesFromGroup(List<T> group) {
		return group.stream()
				.filter(choice -> !isStrictlyWorseThanOthers(choice, group))
				.toList();
	}

	private boolean isStrictlyWorseThanOthers(T choice, List<T> choices) {
		return choices.stream()
				.filter(otherChoice -> choice != otherChoice)
				.anyMatch(otherChoice -> isStrictlyWorseThan(choice, otherChoice));
	}

	private boolean isStrictlyWorseThan(T choice, T otherChoice) {
		return getAttributes(choice).stream()
				.map(FilterOutWorseChoices::getIdAndCondition)
				.distinct()
				.allMatch(x -> sum(choice, x.id(), x.condition()) < sum(otherChoice, x.id(), x.condition()));
	}

	private double sum(T choice, AttributeId id, AttributeCondition condition) {
		return getAttributes(choice).stream()
				.filter(x -> x.id() == id && x.condition().equals(condition))
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

	private String getGroupAndStatsKey(T choice) {
		return getGroupKey(choice) + "#" + getStatsKey(choice);
	}

	protected abstract Object getGroupKey(T choice);

	private String getStatsKey(T choice) {
		return getAttributes(choice).stream()
				.map(FilterOutWorseChoices::getIdAndCondition)
				.map(Objects::toString)
				.distinct()
				.collect(Collectors.joining("#"));
	}

	private static Attribute getIdAndCondition(Attribute x) {
		return Attribute.of(x.id(), 1, x.condition(), x.levelScaled());
	}
}
