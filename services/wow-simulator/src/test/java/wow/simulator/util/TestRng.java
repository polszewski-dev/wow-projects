package wow.simulator.util;

import lombok.Getter;
import wow.commons.model.effect.component.Event;
import wow.commons.model.spell.Spell;
import wow.simulator.model.rng.Rng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

/**
 * User: POlszewski
 * Date: 2025-01-30
 */
@Getter
public class TestRng implements Rng {
	@Getter
	public static class RollData {
		int rollIdx = -1;
		Map<Integer, Boolean> rollResults = Map.of();
		List<Double> rollChances = new ArrayList<>();

		boolean roll(double chancePct) {
			rollChances.add(chancePct);
			++rollIdx;

			if (chancePct <= 0) {
				return false;
			}

			if (chancePct >= 100) {
				return true;
			}

			return rollResults.getOrDefault(rollIdx, false);
		}

		boolean negativeRoll(double chancePct) {
			rollChances.add(chancePct);
			++rollIdx;

			if (chancePct <= 0) {
				return false;
			}

			if (chancePct >= 100) {
				return true;
			}

			return !rollResults.getOrDefault(rollIdx, false);
		}

		public void setRolls(int[] rollIndices) {
			this.rollResults = IntStream.of(rollIndices)
					.boxed()
					.collect(toMap(
							Function.identity(),
							x -> true
					));
		}
	}

	private final RollData hitRollData = new RollData();
	private final RollData critRollData = new RollData();
	private final RollData eventRollData = new RollData();

	@Override
	public boolean hitRoll(double chancePct, Spell spell) {
		return hitRollData.negativeRoll(chancePct);
	}

	@Override
	public boolean critRoll(double chancePct, Spell spell) {
		return critRollData.roll(chancePct);
	}

	@Override
	public boolean eventRoll(double chancePct, Event event) {
		return eventRollData.roll(chancePct);
	}
}
