package wow.evaluator.model;

import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.evaluator.util.AttributesDiffFinder;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-10-27
 */
public record Upgrade(
		ItemSlotGroup slotGroup,
		List<EquippableItem> itemOption,
		double changePct,
		Player referenceCharacter
) {
	public List<EquippableItem> getItemDifference() {
		return itemOption;
	}

	public AttributesDiff getStatDifference() {
		var finder = new AttributesDiffFinder(referenceCharacter, slotGroup, itemOption);

		return finder.getDiff();
	}
}
