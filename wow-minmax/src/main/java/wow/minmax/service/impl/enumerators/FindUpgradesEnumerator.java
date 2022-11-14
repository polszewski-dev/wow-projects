package wow.minmax.service.impl.enumerators;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.util.AttributeEvaluator;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-13
 */
public class FindUpgradesEnumerator extends ItemVariantEnumerator {
	private final ItemSlotGroup slotGroup;
	private final Attributes withoutSlotGroup;

	public FindUpgradesEnumerator(
			PlayerProfile referenceProfile,
			ItemSlotGroup slotGroup,
			Spell spell,
			ItemService itemService,
			CalculationService calculationService
	) {
		super(referenceProfile, spell, itemService, calculationService);
		this.slotGroup = slotGroup;

		for (ItemSlot slot : slotGroup.getSlots()) {
			workingProfile.getEquipment().set(null, slot);
		}

		this.withoutSlotGroup = AttributeEvaluator.of()
				.addAttributes(workingProfile)
				.nothingToSolve();
	}

	@Override
	protected Comparison getItemScore(EquippableItem... itemOption) {
		Attributes totalStats = getTotalStats(itemOption);
		double dps = calculationService.getSpellStatistics(workingProfile, spell, totalStats).getDps();
		double changePct = 100 * (dps / referenceDps - 1);

		if (changePct <= 0) {
			return null;
		}

		equipItems(workingProfile.getEquipment(), itemOption);
		return newComparison(changePct);
	}

	private Attributes getTotalStats(EquippableItem[] itemOption) {
		AttributeEvaluator evaluator = AttributeEvaluator.of();

		evaluator.addAttributes(withoutSlotGroup);

		for (EquippableItem item : itemOption) {
			evaluator.addAttributes(item);
		}

		return evaluator.solveAllLeaveAbilities();
	}

	private void equipItems(Equipment equipment, EquippableItem[] itemOption) {
		for (int i = 0; i < itemOption.length; i++) {
			EquippableItem item = itemOption[i];
			ItemSlot slot = slotGroup.getSlots().get(i);
			equipment.set(item, slot);
		}
	}

	@Override
	protected Map<ItemType, List<Item>> getItemsByType() {
		return itemService.getCasterItemsByType(
				referenceProfile.getCharacterInfo(), phase, spellSchool
		);
	}
}
