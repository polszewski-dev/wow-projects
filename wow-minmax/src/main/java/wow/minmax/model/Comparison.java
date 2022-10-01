package wow.minmax.model;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.AttributesDiff;

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
		//checkEquipment(possibleEquipment);
	}

	public Equipment getReferenceEquipment() {
		return referenceEquipment;
	}

	public Equipment getPossibleEquipment() {
		return possibleEquipment;
	}

	private void checkEquipment(Equipment possibleEquipment) {
		for (ItemSlot slot : ItemSlot.values()) {
			EquippableItem referenceItem = referenceEquipment.get(slot);
			EquippableItem possibleItem = possibleEquipment.get(slot);
			if (referenceItem == null && possibleItem == null) {
				continue;
			}
			if (referenceItem != null && possibleItem == null || referenceItem == null && possibleItem != null) {
				if (slot == ItemSlot.OffHand) {
					continue;
				}
				System.err.println("Empty slot: " + slot);
			}
			if (isEnchantMissing(referenceItem) || isEnchantMissing(possibleItem)) {
				System.err.println("Missing enchant: " + slot);
			}
		}
	}

	private boolean isEnchantMissing(EquippableItem item) {
		return item != null && item.isEnchantable() && item.getEnchant() == null;
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

		return AttributesBuilder.diff(possibleAttributes, referenceAttributes);
	}
}
