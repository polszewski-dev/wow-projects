package wow.minmax.service.impl.enumerators;

import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.CalculationService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class BestItemVariantEnumerator extends ItemVariantEnumerator {
	private final Item item;

	public BestItemVariantEnumerator(
			Character referenceCharacter,
			ItemSlot slot,
			Rotation rotation,
			ItemService itemService,
			CalculationService calculationService,
			MinmaxConfigRepository minmaxConfigRepository
	) {
		super(referenceCharacter, ItemSlotGroup.getGroup(slot).orElseThrow(), rotation, itemService, calculationService, minmaxConfigRepository);
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
