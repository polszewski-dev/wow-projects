package wow.minmax.service.impl.enumerator;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;

/**
 * User: POlszewski
 * Date: 2022-11-23
 */
public abstract class FilterOutWorseChoices<T> {
	private final List<T> nonModifiers;
	private final List<T> modifiers;

	protected FilterOutWorseChoices(List<T> choices) {
		var isModifier = choices.stream().collect(partitioningBy(
				x -> getEffects(x).stream().allMatch(Effect::hasModifierComponentOnly)
		));
		this.nonModifiers = isModifier.get(false);
		this.modifiers = isModifier.get(true);
	}

	public List<T> getResult() {
		var groups = modifiers.stream()
				.collect(groupingBy(this::getGroupKey))
				.values();

		var filteredModifiers = groups.stream()
				.map(this::getAttributeTotals)
				.map(this::filterOutWorseTotals)
				.map(this::removeTheSameTotals)
				.flatMap(Collection::stream);

		return Stream.concat(nonModifiers.stream(), filteredModifiers).toList();
	}

	private List<AttributeTotal<T>> filterOutWorseTotals(List<AttributeTotal<T>> group) {
		return group.stream()
				.filter(total -> !total.isStrictlyWorseThanAnyFrom(group))
				.toList();
	}

	private List<T> removeTheSameTotals(List<AttributeTotal<T>> group) {
		return group.stream()
				.collect(groupingBy(AttributeTotal::getGroupKey))
				.values().stream()
				.map(this::removeBasedOnSource)
				.toList();
	}

	private T removeBasedOnSource(List<AttributeTotal<T>> choices) {
		return choices.stream()
				.min(comparingInt(total -> getOrderWithinTheSameStatTotal(total.choice())))
				.map(AttributeTotal::choice)
				.orElseThrow();
	}

	protected abstract int getOrderWithinTheSameStatTotal(T choice);

	protected abstract Object getGroupKey(T choice);

	private record AttributeKey(
			AttributeId id,
			AttributeCondition condition,
			AttributeScaling scaling
	) {
		AttributeKey(Attribute x) {
			this(x.id(), x.condition(), x.scaling());
		}

		boolean matches(Attribute x) {
			return x.id() == id() && x.condition().equals(condition()) && x.scaling().equals(scaling());
		}
	}

	private record AttributeTotal<T>(
			T choice,
			List<AttributeKey> keys,
			double[] values
	) {
		boolean isStrictlyWorseThan(AttributeTotal<T> other) {
			int ltCnt = 0;

			for (int i = 0; i < values.length; i++) {
				var thisValue = this.values[i];
				var otherValue = other.values[i];

				if (thisValue < otherValue) {
					++ltCnt;
				} else if (thisValue > otherValue) {
					return false;
				}
			}

			return ltCnt > 0;
		}

		boolean isStrictlyWorseThanAnyFrom(List<AttributeTotal<T>> others) {
			return others.stream()
					.filter(other -> this != other)
					.anyMatch(this::isStrictlyWorseThan);
		}

		String getGroupKey() {
			return Arrays.toString(values);
		}
	}

	protected abstract List<Effect> getEffects(T choice);

	private List<Attribute> getAttributes(T choice) {
		return getEffects(choice).stream()
				.flatMap(x -> x.getModifierAttributeList().stream())
				.toList();
	}

	private List<AttributeKey> getAttributeKeys(List<T> group) {
		return group.stream()
				.map(this::getAttributes)
				.flatMap(Collection::stream)
				.map(AttributeKey::new)
				.distinct()
				.toList();
	}

	private AttributeTotal<T> getAttributeTotal(List<AttributeKey> keys, T choice) {
		var values = new double[keys.size()];

		for (int i = 0; i < values.length; i++) {
			values[i] = getTotal(choice, keys.get(i));
		}

		return new AttributeTotal<>(choice, keys, values);
	}

	private double getTotal(T choice, AttributeKey key) {
		return getAttributes(choice).stream()
				.filter(key::matches)
				.mapToDouble(Attribute::value)
				.sum();
	}

	private List<AttributeTotal<T>> getAttributeTotals(List<T> group) {
		var keys = getAttributeKeys(group);

		return group.stream()
				.map(x -> getAttributeTotal(keys, x))
				.toList();
	}
}
