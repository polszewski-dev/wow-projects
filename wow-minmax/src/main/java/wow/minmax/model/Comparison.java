package wow.minmax.model;

import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attributes;
import wow.minmax.util.AttributesDiff;
import wow.minmax.util.AttributesDiffFinder;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-10-27
 */
public record Comparison(
		Equipment possibleEquipment,
		Equipment referenceEquipment,
		Percent changePct
) {
	public List<EquippableItem> getItemDifference() {
		return possibleEquipment.getItemDifference(referenceEquipment);
	}

	public AttributesDiff getStatDifference() {
		Attributes possibleAttributes = possibleEquipment.getStats();
		Attributes referenceAttributes = referenceEquipment.getStats();

		return new AttributesDiffFinder(possibleAttributes, referenceAttributes).getDiff();
	}
}
