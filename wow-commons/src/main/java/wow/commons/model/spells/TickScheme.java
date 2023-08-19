package wow.commons.model.spells;

import wow.commons.model.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public record TickScheme(
		Duration tickInterval,
		Duration duration,
		List<Integer> tickWeights,
		int weightSum
) {
	public TickScheme {
		Objects.requireNonNull(tickInterval);
		Objects.requireNonNull(duration);
		Objects.requireNonNull(tickWeights);
		if (tickWeights.stream().anyMatch(x -> x < 0)) {
			throw new IllegalArgumentException();
		}
	}

	public static TickScheme create(int numTicks, Duration tickInterval) {
		return getTickScheme(getWeights(numTicks), tickInterval);
	}

	public static TickScheme create(List<Integer> weights, Duration tickInterval) {
		return getTickScheme(List.copyOf(weights), tickInterval);
	}

	private static TickScheme getTickScheme(List<Integer> weights, Duration tickInterval) {
		return new TickScheme(
				tickInterval,
				tickInterval.multiplyBy(weights.size()),
				weights,
				weights.stream().mapToInt(x -> x).sum()
		);
	}

	public int numTicks() {
		return tickWeights.size();
	}

	public int weight(int weightNo) {
		return tickWeights.get(weightNo);
	}

	public double scale(double amount, int weightNo) {
		int weight = weight(weightNo);
		return (int) (amount * weight / weightSum);
	}

	private static List<Integer> getWeights(int numTicks) {
		return IntStream.generate(() -> 1)
				.limit(numTicks)
				.boxed()
				.toList();
	}

	public TickScheme adjustBaseDuration(Duration newDuration) {
		int tickDifference = (int) newDuration.subtract(duration).divideBy(tickInterval);

		if (tickDifference == 0) {
			return this;
		}

		return create(newWeights(tickDifference), tickInterval);
	}

	private List<Integer> newWeights(int tickDifference) {
		if (tickDifference > 0) {
			return addTicks(tickDifference);
		} else {
			return removeTicks(tickDifference);
		}
	}

	private List<Integer> addTicks(int tickDifference) {
		List<Integer> newWeights = new ArrayList<>(tickWeights);
		Integer lastWeight = tickWeights.get(tickWeights.size() - 1);
		for (int i = 0; i < tickDifference; ++i) {
			newWeights.add(lastWeight);
		}
		return newWeights;
	}

	private List<Integer> removeTicks(int tickDifference) {
		return tickWeights.subList(0, tickWeights.size() + tickDifference);
	}
}
