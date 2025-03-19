package wow.evaluator.util;

import wow.character.model.build.Rotation;
import wow.commons.model.spell.Ability;
import wow.evaluator.model.RotationSpellStats;
import wow.evaluator.model.RotationStats;
import wow.evaluator.model.Snapshot;

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
