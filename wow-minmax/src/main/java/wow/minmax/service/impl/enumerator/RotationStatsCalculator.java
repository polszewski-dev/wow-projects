package wow.minmax.service.impl.enumerator;

import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.spell.Spell;
import wow.minmax.model.RotationSpellStats;
import wow.minmax.model.RotationStats;
import wow.minmax.service.CalculationService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
public class RotationStatsCalculator extends RotationDpsCalculator {
	private final List<RotationSpellStats> statList = new ArrayList<>();

	public RotationStatsCalculator(Character character, Rotation rotation, Attributes totalStats, CalculationService calculationService) {
		super(character, rotation, totalStats, calculationService);
	}

	@Override
	protected void onRotationSpell(Spell spell, double numCasts, double damage, Snapshot snapshot) {
		statList.add(new RotationSpellStats(spell, numCasts, damage));
	}

	public RotationStats getStats() {
		return new RotationStats(statList, getDps(), getTotalDamage());
	}
}
