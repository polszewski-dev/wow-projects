package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.config.UpgradeConfig;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;
import wow.minmax.service.UpgradeService;
import wow.minmax.service.impl.enumerators.BestItemVariantEnumerator;
import wow.minmax.service.impl.enumerators.FindUpgradesEnumerator;

import java.util.List;
import java.util.stream.Collectors;

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
	public List<Comparison> findUpgrades(PlayerProfile playerProfile, ItemSlotGroup slotGroup, Spell spell) {
		FindUpgradesEnumerator enumerator = new FindUpgradesEnumerator(
				playerProfile, slotGroup, spell, itemService, calculationService
		);

		return enumerator.run().getResult().stream()
				.limit(upgradeConfig.getMaxUpgrades())
				.collect(Collectors.toList());
	}

	@Override
	public EquippableItem getBestItemVariant(PlayerProfile playerProfile, Item item, ItemSlot slot, Spell spell) {
		PlayerProfile referenceProfile = playerProfile.copy();

		referenceProfile.equip(new EquippableItem(item), slot);

		BestItemVariantEnumerator enumerator = new BestItemVariantEnumerator(
				referenceProfile, slot, spell, itemService, calculationService
		);

		return enumerator.run()
				.getResult().get(0)
				.getPossibleEquipment().get(slot);
	}
}
