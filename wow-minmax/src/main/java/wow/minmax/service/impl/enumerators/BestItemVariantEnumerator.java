package wow.minmax.service.impl.enumerators;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class BestItemVariantEnumerator extends ItemVariantEnumerator {
	private final Item item;

	public BestItemVariantEnumerator(
			PlayerProfile referenceProfile,
			ItemSlot slot,
			Spell spell,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceProfile, ItemSlotGroup.getGroup(slot).orElseThrow(), spell, itemService, calculationService);
		this.item = referenceProfile.getEquippedItem(slot).getItem();
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
