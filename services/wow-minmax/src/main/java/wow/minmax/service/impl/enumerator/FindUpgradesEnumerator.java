package wow.minmax.service.impl.enumerator;

import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class FindUpgradesEnumerator extends ItemVariantEnumerator {
	private final ItemFilter itemFilter;

	public FindUpgradesEnumerator(
			PlayerCharacter referenceCharacter,
			ItemSlotGroup slotGroup,
			ItemFilter itemFilter,
			ItemService itemService,
			CalculationService calculationService,
			MinmaxConfigRepository minmaxConfigRepository
	) {
		super(referenceCharacter, slotGroup, itemService, calculationService, minmaxConfigRepository);
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
