package wow.minmax.service.impl.enumerators;

import lombok.AllArgsConstructor;
import wow.character.model.character.Character;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.commons.util.AttributesBuilder;
import wow.minmax.service.CalculationService;

/**
 * User: POlszewski
 * Date: 2023-03-20
 */
@AllArgsConstructor
public class StatEquivalentFinder {
	private static final double PRECISION = 0.0001;

	private final Attributes attributesToFindEquivalent;
	private final PrimitiveAttributeId targetStat;
	private final CalculationService.EquivalentMode mode;
	private final Character character;
	private final Spell spell;
	private final Attributes totalStats;
	private final CalculationService calculationService;

	public Attributes getDpsStatEquivalent() {
		Attributes baseStats = getBaseAttributes();
		double targetDps = getTargetDps();

		double equivalentValue = 0;
		double increase = 1;

		while (true) {
			PrimitiveAttribute equivalentStat = Attribute.of(targetStat, equivalentValue + increase);
			Attributes equivalentStats = AttributesBuilder.addAttribute(baseStats, equivalentStat);
			double equivalentDps = calculationService.getSpellDps(character, spell, equivalentStats);

			if (Math.abs(equivalentDps - targetDps) <= PRECISION) {
				return Attributes.of(targetStat, equivalentValue);
			}

			if (equivalentDps < targetDps) {
				equivalentValue += increase;
			} else {
				increase /= 2;
			}
		}
	}

	private Attributes getBaseAttributes() {
		if (mode == CalculationService.EquivalentMode.REPLACEMENT) {
			return AttributesBuilder.removeAttributes(totalStats, attributesToFindEquivalent);
		}
		return totalStats;
	}

	private double getTargetDps() {
		Attributes targetStats = getTargetStats(attributesToFindEquivalent, totalStats, mode);
		return calculationService.getSpellDps(character, spell, targetStats);
	}

	private static Attributes getTargetStats(Attributes attributesToFindEquivalent, Attributes totalStats, CalculationService.EquivalentMode mode) {
		if (mode == CalculationService.EquivalentMode.REPLACEMENT) {
			return totalStats;
		}
		return AttributesBuilder.addAttributes(totalStats, attributesToFindEquivalent);
	}
}