package wow.minmax.service.impl;

import wow.commons.model.Percent;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.SpellSchool;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.service.ItemService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.categorization.ItemType.*;

/**
 * User: POlszewski
 * Date: 2022-01-08
 */
abstract class ItemVariantEnumerator {
	private final ItemService itemService;
	private final Map<String, Comparison> bestOptions = new HashMap<>();

	protected ItemVariantEnumerator(ItemService itemService) {
		this.itemService = itemService;
	}

	public ItemVariantEnumerator run(ItemSlotGroup slotGroup, PlayerProfile playerProfile, Spell spell) {
		if (slotGroup == ItemSlotGroup.WEAPONS) {
			enumerateWeapons(playerProfile, spell);
		} else if (slotGroup == ItemSlotGroup.FINGERS) {
			enumerateFingers(playerProfile, spell);
		} else if (slotGroup == ItemSlotGroup.TRINKETS) {
			enumerateTrinkets(playerProfile, spell);
		} else {
			enumerateEverythingElse(slotGroup, playerProfile, spell);
		}
		return this;
	}

	private void enumerateWeapons(PlayerProfile playerProfile, Spell spell) {
		for (EquippableItem twoHand : getItemVariants(TWO_HAND, playerProfile, spell)) {
			handleItemOption(twoHand);
		}

		List<EquippableItem> mainHands = getItemVariants(Set.of(MAIN_HAND, ONE_HAND), playerProfile, spell);
		List<EquippableItem> offHands = getItemVariants(OFF_HAND, playerProfile, spell);

		for (EquippableItem mainhand : mainHands) {
			for (EquippableItem offhand : offHands) {
				handleItemOption(mainhand, offhand);
			}
		}
	}

	private void enumerateFingers(PlayerProfile playerProfile, Spell spell) {
		List<EquippableItem> rings1 = getItemVariants(FINGER, playerProfile, spell);
		List<EquippableItem> rings2 = getItemVariants(FINGER, playerProfile, spell);

		for (int i = 0; i < rings1.size(); i++) {
			EquippableItem ring1 = rings1.get(i);
			for (int j = i; j < rings2.size(); j++) {
				EquippableItem ring2 = rings2.get(j);
				if (i != j || !ring1.isUnique()) {
					handleItemOption(ring1, ring2);
				}
			}
		}
	}

	private void enumerateTrinkets(PlayerProfile playerProfile, Spell spell) {
		List<EquippableItem> trinkets = getItemVariants(TRINKET, playerProfile, spell);

		for (int i = 0; i < trinkets.size(); i++) {
			EquippableItem trinket1 = trinkets.get(i);
			for (int j = i; j < trinkets.size(); j++) {
				EquippableItem trinket2 = trinkets.get(j);
				if (i != j || !trinket1.isUnique()) {
					handleItemOption(trinket1, trinket2);
				}
			}
		}
	}

	private void enumerateEverythingElse(ItemSlotGroup slotGroup, PlayerProfile playerProfile, Spell spell) {
		if (slotGroup.getSlots().size() != 1) {
			throw new IllegalArgumentException(slotGroup + " should have only 1 slot");
		}

		ItemSlot slot = slotGroup.getSlots().get(0);
		Set<ItemType> itemTypes = slot.getItemTypes();

		for (EquippableItem item : getItemVariants(itemTypes, playerProfile, spell)) {
			handleItemOption(item);
		}
	}

	public List<Comparison> getResult() {
		return bestOptions.values()
						  .stream()
						  .sorted(Comparator.<Comparison, Percent>comparing(x -> x.changePct).reversed())
						  .collect(Collectors.toList());
	}

	private void handleItemOption(EquippableItem... itemOption) {
		Comparison comparison = getItemScore(itemOption);

		if (comparison == null) {
			return;
		}

		String key = getUniqueItemKey(itemOption);
		Comparison currentBestComparison = bestOptions.get(key);

		if (currentBestComparison == null || comparison.changePct.compareTo(currentBestComparison.changePct) > 0) {
			bestOptions.put(key, comparison);
		}
	}

	private String getUniqueItemKey(EquippableItem... itemOption) {
		return Stream.of(itemOption)
					 .map(EquippableItem::getName)
					 .sorted()
					 .collect(Collectors.joining());
	}

	protected abstract Comparison getItemScore(EquippableItem... itemOption);

	private List<EquippableItem> getItemVariants(ItemType itemType, PlayerProfile playerProfile, Spell spell) {
		return getItemVariants(Set.of(itemType), playerProfile, spell);
	}

	private List<EquippableItem> getItemVariants(Set<ItemType> itemTypes, PlayerProfile playerProfile, Spell spell) {
		Map<ItemType, List<Item>> itemsByType = getItemsByType(playerProfile, spell);
		List<EquippableItem> result = new ArrayList<>();

		for (ItemType itemType : itemTypes) {
			for (Item item : itemsByType.getOrDefault(itemType, List.of())) {
				result.addAll(getItemVariants(item, playerProfile.getPhase(), spell.getSpellSchool()));
			}
		}

		return result;
	}

	protected abstract Map<ItemType, List<Item>> getItemsByType(PlayerProfile playerProfile, Spell spell);

	private List<EquippableItem> getItemVariants(Item item, Phase phase, SpellSchool spellSchool) {
		if (item.isEnchantable() && item.hasSockets()) {
			List<Enchant> enchants = itemService.getCasterEnchants(item.getItemType(), phase, spellSchool);
			List<Gem[]> gemCombos = itemService.getCasterGemCombos(item, phase);
			List<EquippableItem> result = new ArrayList<>(enchants.size() * gemCombos.size());

			for (Enchant enchant : enchants) {
				for (Gem[] gemCombo : gemCombos) {
					result.add(new EquippableItem(item).enchant(enchant).gem(gemCombo));
				}
			}
			return result;
		} else if (item.hasSockets()) {
			List<Gem[]> gemCombos = itemService.getCasterGemCombos(item, phase);
			List<EquippableItem> result = new ArrayList<>(gemCombos.size());

			for (Gem[] gemCombo : gemCombos) {
				result.add(new EquippableItem(item).gem(gemCombo));
			}
			return result;
		} else if (item.isEnchantable()) {
			List<Enchant> enchants = itemService.getCasterEnchants(item.getItemType(), phase, spellSchool);
			List<EquippableItem> result = new ArrayList<>(enchants.size());

			for (Enchant enchant : enchants) {
				result.add(new EquippableItem(item).enchant(enchant));
			}
			return result;
		} else {
			return List.of(new EquippableItem(item));
		}
	}
}
