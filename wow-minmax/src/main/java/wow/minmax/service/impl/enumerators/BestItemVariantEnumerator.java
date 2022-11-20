package wow.minmax.service.impl.enumerators;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class BestItemVariantEnumerator extends ItemVariantEnumerator {
	private final ItemSlot slot;
	private final Item item;

	public BestItemVariantEnumerator(
			PlayerProfile referenceProfile,
			ItemSlot slot,
			Item item,
			Spell spell,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceProfile, spell, itemService, calculationService);
		this.slot = slot;
		this.item = item;
	}

	@Override
	protected Comparison getItemScore(EquippableItem... itemOption) {
		EquippableItem itemVariant = itemOption[0];
		workingProfile.getEquipment().set(itemVariant, slot);

		double dps = calculationService.getSpellStatistics(workingProfile, spell).getDps();
		double changePct = 100 * (dps / referenceDps - 1);

		return newComparison(changePct);
	}

	@Override
	protected List<Item> getItemsToAnalyze(ItemType itemType) {
		return List.of(item);
	}
}
