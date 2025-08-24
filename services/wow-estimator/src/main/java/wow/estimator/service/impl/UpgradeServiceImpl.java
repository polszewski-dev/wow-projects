package wow.estimator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.estimator.model.Player;
import wow.estimator.model.Upgrade;
import wow.estimator.service.CalculationService;
import wow.estimator.service.ItemService;
import wow.estimator.service.UpgradeService;
import wow.estimator.util.BestItemVariantEnumerator;
import wow.estimator.util.FindUpgradesEnumerator;

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
	public List<Upgrade> findUpgrades(Player player, ItemSlotGroup slotGroup, ItemFilter itemFilter, ItemLevelFilter itemLevelFilter, GemFilter gemFilter, Set<String> enchantNames, int maxUpgrades) {
		var enumerator = new FindUpgradesEnumerator(
				player, slotGroup, itemFilter, itemLevelFilter, gemFilter, enchantNames, itemService, calculationService
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
