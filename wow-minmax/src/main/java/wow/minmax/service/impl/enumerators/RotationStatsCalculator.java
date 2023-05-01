package wow.minmax.service.impl.enumerators;

import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.character.service.CharacterCalculationService;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;
import wow.minmax.model.RotationSpellStats;
import wow.minmax.model.RotationStats;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
public class RotationStatsCalculator extends RotationDpsCalculator {
	private final List<RotationSpellStats> statList = new ArrayList<>();

	public RotationStatsCalculator(Character character, Rotation rotation, Attributes totalStats, CharacterCalculationService characterCalculationService) {
		super(character, rotation, totalStats, characterCalculationService);
	}

	@Override
	protected void onRotationSpell(Spell spell, double numCasts, double damage, Snapshot snapshot) {
		statList.add(new RotationSpellStats(spell, numCasts, damage));
	}

	public RotationStats getStats() {
		return new RotationStats(statList, getDps(), getTotalDamage());
	}
}
