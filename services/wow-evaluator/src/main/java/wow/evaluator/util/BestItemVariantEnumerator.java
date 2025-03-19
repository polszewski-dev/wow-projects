package wow.evaluator.util;

import wow.character.model.equipment.GemFilter;
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
public class BestItemVariantEnumerator extends ItemVariantEnumerator {
	private final Item item;

	public BestItemVariantEnumerator(
			Player referenceCharacter,
			ItemSlot slot,
			GemFilter gemFilter,
			Set<String> enchantNames,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceCharacter, ItemSlotGroup.getGroup(slot).orElseThrow(), gemFilter, enchantNames, itemService, calculationService);
		this.item = referenceCharacter.getEquippedItem(slot).getItem();
	}

	@Override
	protected boolean isAcceptable(double changePct) {
		return changePct >= 0;
	}

	@Override
	protected List<Item> getItemsToAnalyze(ItemSlot itemSlot) {
		return List.of(item);
	}
}
