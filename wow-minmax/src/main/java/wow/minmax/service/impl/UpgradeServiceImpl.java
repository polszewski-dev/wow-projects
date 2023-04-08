package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.config.UpgradeConfig;
import wow.minmax.model.Comparison;
import wow.minmax.service.CalculationService;
import wow.minmax.service.UpgradeService;
import wow.minmax.service.impl.enumerators.BestItemVariantEnumerator;
import wow.minmax.service.impl.enumerators.FindUpgradesEnumerator;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class UpgradeServiceImpl implements UpgradeService {
	private final UpgradeConfig upgradeConfig;

	private final ItemService itemService;
	private final CalculationService calculationService;

	@Override
	public List<Comparison> findUpgrades(Character character, ItemSlotGroup slotGroup, ItemFilter itemFilter) {
		return findUpgrades(character, slotGroup, itemFilter, character.getRotation());
	}

	@Override
	public List<Comparison> findUpgrades(Character character, ItemSlotGroup slotGroup, ItemFilter itemFilter, Rotation rotation) {
		FindUpgradesEnumerator enumerator = new FindUpgradesEnumerator(
				character, slotGroup, itemFilter, character.getRotation(), itemService, calculationService
		);

		return enumerator.run().getResult().stream()
				.limit(upgradeConfig.getMaxUpgrades())
				.toList();
	}

	@Override
	public EquippableItem getBestItemVariant(Character character, Item item, ItemSlot slot) {
		return getBestItemVariant(character, item, slot, character.getRotation());
	}

	@Override
	public EquippableItem getBestItemVariant(Character character, Item item, ItemSlot slot, Rotation rotation) {
		Character referenceCharacter = character.copy();

		referenceCharacter.equip(new EquippableItem(item), slot);

		BestItemVariantEnumerator enumerator = new BestItemVariantEnumerator(
				referenceCharacter, slot, rotation, itemService, calculationService
		);

		return enumerator.run()
				.getResult().get(0)
				.getPossibleEquipment().get(slot);
	}
}
