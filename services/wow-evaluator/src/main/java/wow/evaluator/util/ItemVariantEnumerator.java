package wow.evaluator.util;

import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Item;
import wow.evaluator.model.Player;
import wow.evaluator.model.Upgrade;
import wow.evaluator.service.CalculationService;
import wow.evaluator.service.ItemService;

import java.util.*;
import java.util.stream.Collectors;

import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2022-01-08
 */
public abstract class ItemVariantEnumerator {
	protected final Player referenceCharacter;
	private final ItemSlotGroup slotGroup;
	private final GemFilter gemFilter;
	private final Set<String> enchantNames;

	private final CharacterUpgradeContext characterUpgradeContext;
	protected final ItemService itemService;

	private final Map<String, Upgrade> bestUpgrades = new HashMap<>();

	protected ItemVariantEnumerator(
			Player referenceCharacter,
			ItemSlotGroup slotGroup,
			GemFilter gemFilter,
			Set<String> enchantNames,
			ItemService itemService,
			CalculationService calculationService
	) {
		this.referenceCharacter = referenceCharacter;
		this.slotGroup = slotGroup;
		this.gemFilter = gemFilter;
		this.enchantNames = enchantNames;

		this.characterUpgradeContext = new CharacterUpgradeContext(referenceCharacter, slotGroup, calculationService);
		this.itemService = itemService;
	}

	public ItemVariantEnumerator run() {
		switch (slotGroup) {
			case WEAPONS -> enumerateWeapons();
			case FINGERS -> enumerateFingers();
			case TRINKETS -> enumerateTrinkets();
			default -> enumerateEverythingElse(slotGroup);
		}
		return this;
	}

	private void enumerateWeapons() {
		var isTwoHander = getItemVariants(MAIN_HAND).stream()
				.collect(Collectors.partitioningBy(item -> item.getItemType() == ItemType.TWO_HAND));

		var twoHands = isTwoHander.getOrDefault(true, List.of());

		for (var twoHand : twoHands) {
			handleItemOption(twoHand);
		}

		var mainHands = isTwoHander.getOrDefault(false, List.of());
		var offHands = getItemVariants(OFF_HAND);

		for (var mainhand : mainHands) {
			for (var offhand : offHands) {
				handleItemOption(mainhand, offhand);
			}
		}
	}

	private void enumerateFingers() {
		var rings = getItemVariants(FINGER_1);

		for (int i = 0; i < rings.size(); i++) {
			var ring1 = rings.get(i);
			for (int j = i; j < rings.size(); j++) {
				var ring2 = rings.get(j);
				if (i != j || !ring1.isUnique()) {
					handleItemOption(ring1, ring2);
				}
			}
		}
	}

	private void enumerateTrinkets() {
		var trinkets = getItemVariants(TRINKET_1);

		for (int i = 0; i < trinkets.size(); i++) {
			var trinket1 = trinkets.get(i);
			for (int j = i; j < trinkets.size(); j++) {
				var trinket2 = trinkets.get(j);
				if (i != j || !trinket1.isUnique()) {
					handleItemOption(trinket1, trinket2);
				}
			}
		}
	}

	private void enumerateEverythingElse(ItemSlotGroup slotGroup) {
		if (slotGroup.getSlots().size() != 1) {
			throw new IllegalArgumentException(slotGroup + " should have only 1 slot");
		}

		var slot = slotGroup.getSlots().getFirst();

		for (var item : getItemVariants(slot)) {
			handleItemOption(item);
		}
	}

	public List<Upgrade> getResult() {
		return bestUpgrades.values()
						  .stream()
						  .sorted(Comparator.comparingDouble(Upgrade::changePct).reversed())
						  .toList();
	}

	private void handleItemOption(EquippableItem... itemOption) {
		var upgrade = getUpgrade(itemOption);

		if (upgrade == null) {
			return;
		}

		var key = getUniqueItemOptionKey(itemOption);
		var currentBestUpgrade = bestUpgrades.get(key);

		if (currentBestUpgrade == null || upgrade.changePct() > currentBestUpgrade.changePct()) {
			bestUpgrades.put(key, upgrade);
		}
	}

	private Upgrade getUpgrade(EquippableItem... itemOption) {
		var changePct = characterUpgradeContext.getChangePct(itemOption);

		if (!isAcceptable(changePct)) {
			return null;
		}

		return characterUpgradeContext.getUpgrade(itemOption, changePct);
	}

	protected abstract boolean isAcceptable(double changePct);

	private String getUniqueItemOptionKey(EquippableItem... itemOption) {
		return switch (itemOption.length) {
			case 1 -> itemOption[0].getName();
			case 2 -> {
				var n1 = itemOption[0].getName();
				var n2 = itemOption[1].getName();
				yield (n1.compareTo(n2) <= 0) ? n1 + n2 : n2 + n1;
			}
			default -> throw new IllegalArgumentException();
		};
	}

	private List<EquippableItem> getItemVariants(ItemSlot slot) {
		return getItemVariants(Set.of(slot));
	}

	private List<EquippableItem> getItemVariants(Set<ItemSlot> slots) {
		var result = new ArrayList<EquippableItem>();

		for (var slot : slots) {
			for (var item : getItemsToAnalyze(slot)) {
				result.addAll(getItemVariants(item));
			}
		}

		return result;
	}

	protected abstract List<Item> getItemsToAnalyze(ItemSlot slot);

	private List<EquippableItem> getItemVariants(Item item) {
		var enchants = getEnchants(item);

		if (!enchants.isEmpty() && item.hasSockets()) {
			return getEnchantedAndGemmedItems(item, enchants);
		} else if (item.hasSockets()) {
			return getGemmedItems(item);
		} else if (!enchants.isEmpty()) {
			return getEnchantedItems(item, enchants);
		} else {
			return List.of(new EquippableItem(item));
		}
	}

	private List<Enchant> getEnchants(Item item) {
		return itemService.getBestEnchants(referenceCharacter, item.getItemType(), item.getItemSubType()).stream()
				.filter(x -> enchantNames.contains(x.getName()))
				.toList();
	}

	private List<EquippableItem> getEnchantedAndGemmedItems(Item item, List<Enchant> enchants) {
		var gemCombos = itemService.getBestGemCombos(referenceCharacter, item, slotGroup, gemFilter);
		var result = new ArrayList<EquippableItem>(enchants.size() * gemCombos.size());

		for (var enchant : enchants) {
			for (var gemCombo : gemCombos) {
				result.add(new EquippableItem(item).enchant(enchant).gem(gemCombo));
			}
		}

		return result;
	}

	private List<EquippableItem> getGemmedItems(Item item) {
		var gemCombos = itemService.getBestGemCombos(referenceCharacter, item, slotGroup, gemFilter);
		var result = new ArrayList<EquippableItem>(gemCombos.size());

		for (var gemCombo : gemCombos) {
			result.add(new EquippableItem(item).gem(gemCombo));
		}

		return result;
	}

	private List<EquippableItem> getEnchantedItems(Item item, List<Enchant> enchants) {
		if (enchants.isEmpty()) {
			return List.of(new EquippableItem(item));
		}

		var result = new ArrayList<EquippableItem>(enchants.size());

		for (var enchant : enchants) {
			result.add(new EquippableItem(item).enchant(enchant));
		}

		return result;
	}
}
