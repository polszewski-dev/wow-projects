package wow.minmax.service.impl.enumerators;

import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class FindUpgradesEnumerator extends ItemVariantEnumerator {
	public FindUpgradesEnumerator(
			PlayerProfile referenceProfile,
			ItemSlotGroup slotGroup,
			Spell spell,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceProfile, slotGroup, spell, itemService, calculationService);
	}

	@Override
	protected boolean isAcceptable(double changePct) {
		return changePct > 0;
	}

	@Override
	protected List<Item> getItemsToAnalyze(ItemSlot itemSlot) {
		return itemService.getItemsBySlot(referenceProfile.getCharacter(), itemSlot);
	}
}
