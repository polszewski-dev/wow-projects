package wow.minmax.service.impl.enumerator;

import wow.character.model.character.PlayerCharacter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.repository.MinmaxConfigRepository;
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
			PlayerCharacter referenceCharacter,
			ItemSlot slot,
			ItemService itemService,
			CalculationService calculationService,
			MinmaxConfigRepository minmaxConfigRepository
	) {
		super(referenceCharacter, ItemSlotGroup.getGroup(slot).orElseThrow(), itemService, calculationService, minmaxConfigRepository);
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
