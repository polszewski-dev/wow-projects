package wow.character.service.impl.enumerators;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.AbstractMap.SimpleImmutableEntry;

/**
 * User: POlszewski
 * Date: 2022-11-23
 */
public abstract class FilterOutWorseChoices<T extends AttributeSource> {
	private final List<T> choices;

	protected FilterOutWorseChoices(List<T> choices) {
		this.choices = choices;
	}

	public List<T> getResult() {
		return choices.stream()
				.collect(Collectors.groupingBy(this::getGroupAndStatsKey))
				.values().stream()
				.map(this::filterOutWorseChoicesFromGroup)
				.map(this::removeTheSameStats)
				.flatMap(Collection::stream)
				.toList();
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
		return choice.getPrimitiveAttributeList().stream()
				.map(FilterOutWorseChoices::getIdAndCondition)
				.collect(Collectors.toSet()).stream()
				.allMatch(x -> choice.getDouble(x.getKey(), x.getValue()) < otherChoice.getDouble(x.getKey(), x.getValue()));
	}

	private List<T> removeTheSameStats(List<T> group) {
		return group.stream()
				.collect(Collectors.groupingBy(AttributeSource::statString))
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
		return choice.getPrimitiveAttributeList().stream()
				.map(FilterOutWorseChoices::getIdAndCondition)
				.map(Objects::toString)
				.distinct()
				.collect(Collectors.joining("#"));
	}

	private static SimpleImmutableEntry<PrimitiveAttributeId, AttributeCondition> getIdAndCondition(PrimitiveAttribute x) {
		return new SimpleImmutableEntry<>(x.getId(), x.getCondition());
	}
}
