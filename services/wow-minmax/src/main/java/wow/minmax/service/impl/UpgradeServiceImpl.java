package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.config.UpgradeConfig;
import wow.minmax.model.Player;
import wow.minmax.model.Upgrade;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;
import wow.minmax.service.UpgradeService;
import wow.minmax.service.impl.enumerator.BestItemVariantEnumerator;
import wow.minmax.service.impl.enumerator.FindUpgradesEnumerator;

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

	private final MinmaxConfigRepository minmaxConfigRepository;

	@Override
	public List<Upgrade> findUpgrades(Player player, ItemSlotGroup slotGroup, ItemFilter itemFilter, GemFilter gemFilter) {
		var enumerator = new FindUpgradesEnumerator(
				player, slotGroup, itemFilter, gemFilter, itemService, calculationService, minmaxConfigRepository
		);

		return enumerator.run().getResult().stream()
				.limit(upgradeConfig.getMaxUpgrades())
				.toList();
	}

	@Override
	public EquippableItem getBestItemVariant(Player player, Item item, ItemSlot slot, GemFilter gemFilter) {
		var referenceCharacter = player.copy();

		referenceCharacter.equip(new EquippableItem(item), slot);

		var enumerator = new BestItemVariantEnumerator(
				referenceCharacter, slot, gemFilter, itemService, calculationService, minmaxConfigRepository
		);

		return enumerator.run()
				.getResult().getFirst()
				.itemOption().getFirst();
	}
}
