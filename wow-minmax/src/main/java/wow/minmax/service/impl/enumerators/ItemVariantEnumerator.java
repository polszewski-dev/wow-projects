package wow.minmax.service.impl.enumerators;

import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2022-01-08
 */
public abstract class ItemVariantEnumerator {
	protected final ItemService itemService;
	protected final CalculationService calculationService;

	protected final PlayerProfile referenceProfile;
	private final double referenceDps;
	private final ItemSlotGroup slotGroup;
	private final Spell spell;

	private final PlayerProfile workingProfile;
	private final Attributes withoutSlotGroup;

	private final Map<String, Comparison> bestOptions = new HashMap<>();

	protected ItemVariantEnumerator(
			PlayerProfile referenceProfile, ItemSlotGroup slotGroup, Spell spell, ItemService itemService, CalculationService calculationService) {
		this.itemService = itemService;
		this.calculationService = calculationService;

		this.referenceProfile = referenceProfile;
		this.referenceDps = calculationService.getSpellStatistics(referenceProfile, spell).getDps();
		this.slotGroup = slotGroup;
		this.spell = spell;

		this.workingProfile = referenceProfile.copy();

		for (ItemSlot slot : slotGroup.getSlots()) {
			workingProfile.getEquipment().set(null, slot);
		}

		this.withoutSlotGroup = AttributeEvaluator.of()
				.addAttributes(workingProfile)
				.nothingToSolve();
	}

	public ItemVariantEnumerator run() {
		if (slotGroup == ItemSlotGroup.WEAPONS) {
			enumerateWeapons();
		} else if (slotGroup == ItemSlotGroup.FINGERS) {
			enumerateFingers();
		} else if (slotGroup == ItemSlotGroup.TRINKETS) {
			enumerateTrinkets();
		} else {
			enumerateEverythingElse(slotGroup);
		}
		return this;
	}

	private void enumerateWeapons() {
		var isTwoHander = getItemVariants(MAIN_HAND).stream()
				.collect(Collectors.partitioningBy(item -> item.getItemType() == ItemType.TWO_HAND));

		List<EquippableItem> twoHands = isTwoHander.getOrDefault(true, List.of());

		for (EquippableItem twoHand : twoHands) {
			handleItemOption(twoHand);
		}

		List<EquippableItem> mainHands = isTwoHander.getOrDefault(false, List.of());
		List<EquippableItem> offHands = getItemVariants(OFF_HAND);

		for (EquippableItem mainhand : mainHands) {
			for (EquippableItem offhand : offHands) {
				handleItemOption(mainhand, offhand);
			}
		}
	}

	private void enumerateFingers() {
		List<EquippableItem> rings = getItemVariants(FINGER_1);

		for (int i = 0; i < rings.size(); i++) {
			EquippableItem ring1 = rings.get(i);
			for (int j = i; j < rings.size(); j++) {
				EquippableItem ring2 = rings.get(j);
				if (i != j || !ring1.isUnique()) {
					handleItemOption(ring1, ring2);
				}
			}
		}
	}

	private void enumerateTrinkets() {
		List<EquippableItem> trinkets = getItemVariants(TRINKET_1);

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

	private void enumerateEverythingElse(ItemSlotGroup slotGroup) {
		if (slotGroup.getSlots().size() != 1) {
			throw new IllegalArgumentException(slotGroup + " should have only 1 slot");
		}

		ItemSlot slot = slotGroup.getSlots().get(0);

		for (EquippableItem item : getItemVariants(slot)) {
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
		Comparison comparison = getItemComparison(itemOption);

		if (comparison == null) {
			return;
		}

		String key = getUniqueItemKey(itemOption);
		Comparison currentBestComparison = bestOptions.get(key);

		if (currentBestComparison == null || comparison.changePct.compareTo(currentBestComparison.changePct) > 0) {
			bestOptions.put(key, comparison);
		}
	}

	private Comparison newComparison(double changePct) {
		return new Comparison(workingProfile.getEquipment().copy(), referenceProfile.getEquipment(), Percent.of(changePct));
	}

	private String getUniqueItemKey(EquippableItem... itemOption) {
		return Stream.of(itemOption)
					 .map(EquippableItem::getName)
					 .sorted()
					 .collect(Collectors.joining());
	}

	private Comparison getItemComparison(EquippableItem... itemOption) {
		Attributes totalStats = getTotalStats(itemOption);
		double dps = calculationService.getSpellStatistics(workingProfile, spell, totalStats).getDps();
		double changePct = 100 * (dps / referenceDps - 1);

		if (!isAcceptable(changePct)) {
			return null;
		}

		equipItems(workingProfile.getEquipment(), itemOption);
		return newComparison(changePct);
	}

	protected abstract boolean isAcceptable(double changePct);

	private List<EquippableItem> getItemVariants(ItemSlot slot) {
		return getItemVariants(Set.of(slot));
	}

	private List<EquippableItem> getItemVariants(Set<ItemSlot> slots) {
		List<EquippableItem> result = new ArrayList<>();

		for (ItemSlot slot : slots) {
			for (Item item : getItemsToAnalyze(slot)) {
				result.addAll(getItemVariants(item));
			}
		}

		return result;
	}

	protected abstract List<Item> getItemsToAnalyze(ItemSlot slot);

	private List<EquippableItem> getItemVariants(Item item) {
		if (item.isEnchantable() && item.hasSockets()) {
			return getEnchantedAndGemmedItems(item);
		} else if (item.hasSockets()) {
			return getGemmedItems(item);
		} else if (item.isEnchantable()) {
			return getEnchantedItems(item);
		} else {
			return List.of(new EquippableItem(item));
		}
	}

	private List<EquippableItem> getEnchantedAndGemmedItems(Item item) {
		List<Enchant> enchants = itemService.getBestEnchants(referenceProfile, item.getItemType());
		List<Gem[]> gemCombos = itemService.getBestGemCombos(referenceProfile, item);
		List<EquippableItem> result = new ArrayList<>(enchants.size() * gemCombos.size());

		for (Enchant enchant : enchants) {
			for (Gem[] gemCombo : gemCombos) {
				result.add(new EquippableItem(item).enchant(enchant).gem(gemCombo));
			}
		}
		return result;
	}

	private List<EquippableItem> getGemmedItems(Item item) {
		List<Gem[]> gemCombos = itemService.getBestGemCombos(referenceProfile, item);
		List<EquippableItem> result = new ArrayList<>(gemCombos.size());

		for (Gem[] gemCombo : gemCombos) {
			result.add(new EquippableItem(item).gem(gemCombo));
		}
		return result;
	}

	private List<EquippableItem> getEnchantedItems(Item item) {
		List<Enchant> enchants = itemService.getBestEnchants(referenceProfile, item.getItemType());
		List<EquippableItem> result = new ArrayList<>(enchants.size());

		if (enchants.isEmpty()) {
			return List.of(new EquippableItem(item));
		}

		for (Enchant enchant : enchants) {
			result.add(new EquippableItem(item).enchant(enchant));
		}
		return result;
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
}
