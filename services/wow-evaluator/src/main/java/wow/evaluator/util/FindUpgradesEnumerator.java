package wow.evaluator.util;

import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.evaluator.model.Player;
import wow.evaluator.service.CalculationService;
import wow.evaluator.service.ItemService;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class FindUpgradesEnumerator extends ItemVariantEnumerator {
	private final ItemFilter itemFilter;

	public FindUpgradesEnumerator(
			Player referenceCharacter,
			ItemSlotGroup slotGroup,
			ItemFilter itemFilter,
			GemFilter gemFilter,
			Set<String> enchantNames,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceCharacter, slotGroup, gemFilter, enchantNames, itemService, calculationService);
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
