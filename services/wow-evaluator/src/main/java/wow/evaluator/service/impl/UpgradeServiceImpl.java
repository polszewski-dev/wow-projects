package wow.evaluator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.evaluator.model.Player;
import wow.evaluator.model.Upgrade;
import wow.evaluator.service.CalculationService;
import wow.evaluator.service.ItemService;
import wow.evaluator.service.UpgradeService;
import wow.evaluator.util.BestItemVariantEnumerator;
import wow.evaluator.util.FindUpgradesEnumerator;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class UpgradeServiceImpl implements UpgradeService {
	private final ItemService itemService;
	private final CalculationService calculationService;

	@Override
	public List<Upgrade> findUpgrades(Player player, ItemSlotGroup slotGroup, ItemFilter itemFilter, GemFilter gemFilter, Set<String> enchantNames, int maxUpgrades) {
		var enumerator = new FindUpgradesEnumerator(
				player, slotGroup, itemFilter, gemFilter, enchantNames, itemService, calculationService
		);

		return enumerator.run().getResult().stream()
				.limit(maxUpgrades)
				.toList();
	}

	@Override
	public EquippableItem getBestItemVariant(Player player, Item item, ItemSlot slot, GemFilter gemFilter, Set<String> enchantNames) {
		var referenceCharacter = player.copy();

		referenceCharacter.equip(new EquippableItem(item), slot);

		var enumerator = new BestItemVariantEnumerator(
				referenceCharacter, slot, gemFilter, enchantNames, itemService, calculationService
		);

		return enumerator.run()
				.getResult().getFirst()
				.itemOption().getFirst();
	}
}
