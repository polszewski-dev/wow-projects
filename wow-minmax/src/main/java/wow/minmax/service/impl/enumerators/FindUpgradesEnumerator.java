package wow.minmax.service.impl.enumerators;

import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.service.CalculationService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class FindUpgradesEnumerator extends ItemVariantEnumerator {
	private final ItemFilter itemFilter;

	public FindUpgradesEnumerator(
			Character referenceCharacter,
			ItemSlotGroup slotGroup,
			ItemFilter itemFilter,
			Rotation rotation,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceCharacter, slotGroup, rotation, itemService, calculationService);
		this.itemFilter = itemFilter;
	}

	@Override
	protected boolean isAcceptable(double changePct) {
		return changePct > 0;
	}

	@Override
	protected List<Item> getItemsToAnalyze(ItemSlot itemSlot) {
		return itemService.getItemsBySlot(referenceCharacter, itemSlot, itemFilter);
	}
}
