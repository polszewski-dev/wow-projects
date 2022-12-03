package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;
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
	private final ItemService itemService;
	private final CalculationService calculationService;

	@Override
	public List<Comparison> findUpgrades(PlayerProfile playerProfile, ItemSlotGroup slotGroup, Spell spell) {
		FindUpgradesEnumerator enumerator = new FindUpgradesEnumerator(
				playerProfile, slotGroup, spell, itemService, calculationService
		);

		return enumerator.run(slotGroup).getResult();
	}

	@Override
	public EquippableItem getBestItemVariant(PlayerProfile playerProfile, Item item, ItemSlot slot, Spell spell) {
		BestItemVariantEnumerator enumerator = new BestItemVariantEnumerator(
				playerProfile, slot, item, spell, itemService, calculationService
		);

		return enumerator.run(ItemSlotGroup.getGroup(slot))
				.getResult().get(0)
				.getPossibleEquipment().get(slot);
	}
}
