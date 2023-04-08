package wow.minmax.service.impl.enumerators;

import lombok.AllArgsConstructor;
import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.spells.Spell;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-08
 */
public class RotationAbilityEquivalentCalculator extends RotationDpsCalculator {
	public RotationAbilityEquivalentCalculator(Character character, Rotation rotation, Attributes totalStats) {
		super(character, rotation, totalStats);
	}

	@Override
	protected void onRotationSpell(Spell spell, double numCasts, double damage, Snapshot snapshot) {
		list.add(new SpellData(snapshot, numCasts * damage));
	}

	public Attributes getAbilityEquivalent(SpecialAbility specialAbility) {
		AttributeEvaluator attributeEvaluator = AttributeEvaluator.of();

		for (SpellData data : list) {
			Attributes equivalent = specialAbility.getStatEquivalent(data.snapshot);
			double scaleFactor = data.totalDamage / getTotalDamage();

			attributeEvaluator.addAttributes(equivalent.scale(scaleFactor));
		}

		return attributeEvaluator.solveAllLeaveAbilities();
	}

	@AllArgsConstructor
	private static class SpellData {
		private final Snapshot snapshot;
		private final double totalDamage;
	}

	private final List<SpellData> list = new ArrayList<>();
}
