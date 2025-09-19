package wow.estimator.util;

import wow.commons.model.spell.Ability;
import wow.estimator.model.Rotation;
import wow.estimator.model.RotationSpellStats;
import wow.estimator.model.RotationStats;
import wow.estimator.model.Snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
public class RotationStatsCalculator extends RotationDpsCalculator {
	private final List<RotationSpellStats> statList = new ArrayList<>();

	public RotationStatsCalculator(Rotation rotation, Function<Ability, Snapshot> snapshotFunction) {
		super(rotation, snapshotFunction);
	}

	@Override
	protected void onRotationSpell(Ability ability, double numCasts, double damage, Snapshot snapshot) {
		statList.add(new RotationSpellStats(ability, numCasts, damage));
	}

	public RotationStats getStats() {
		return new RotationStats(statList, getDps(), getTotalDamage());
	}
}
