package wow.commons.model.spell;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public record TickScheme(List<Double> tickWeights) {
	public static final TickScheme DEFAULT = new TickScheme(List.of(1.0));

	public TickScheme {
		Objects.requireNonNull(tickWeights);
		if (tickWeights.stream().anyMatch(x -> x < 0)) {
			throw new IllegalArgumentException();
		}
	}

	public double weight(int tickNo) {
		int idx = tickNo <= tickWeights.size() ? tickNo - 1 : tickWeights.size() - 1;
		return tickWeights.get(idx);
	}
}
