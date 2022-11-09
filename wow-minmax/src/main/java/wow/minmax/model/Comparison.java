package wow.minmax.model;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.AttributesDiff;
import wow.commons.util.AttributesDiffFinder;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-10-27
 */
public class Comparison {
	public final Equipment referenceEquipment;
	public final Equipment possibleEquipment;
	public final Percent changePct;

	public Comparison(Equipment possibleEquipment, Equipment referenceEquipment, Percent changePct) {
		this.referenceEquipment = referenceEquipment;
		this.possibleEquipment = possibleEquipment;
		this.changePct = changePct;
	}

	public Equipment getReferenceEquipment() {
		return referenceEquipment;
	}

	public Equipment getPossibleEquipment() {
		return possibleEquipment;
	}

	public List<EquippableItem> getItemDifference() {
		return possibleEquipment.getItemDifference(referenceEquipment);
	}

	public AttributesDiff getStatDifference() {
		Attributes possibleAttributes = AttributeEvaluator.of()
				.addAttributes(possibleEquipment)
				.solveAllLeaveAbilities()
				.getAttributes();

		Attributes referenceAttributes = AttributeEvaluator.of()
				.addAttributes(referenceEquipment)
				.solveAllLeaveAbilities()
				.getAttributes();

		return new AttributesDiffFinder(possibleAttributes, referenceAttributes).getDiff();
	}
}
