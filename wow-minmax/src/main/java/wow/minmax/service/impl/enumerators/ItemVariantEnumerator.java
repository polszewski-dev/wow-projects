package wow.minmax.service.impl.enumerators;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.commons.util.AttributeEvaluator;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.categorization.ItemType.*;

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
		for (EquippableItem twoHand : getItemVariants(TWO_HAND)) {
			handleItemOption(twoHand);
		}

		List<EquippableItem> mainHands = getItemVariants(Set.of(MAIN_HAND, ONE_HAND));
		List<EquippableItem> offHands = getItemVariants(OFF_HAND);

		for (EquippableItem mainhand : mainHands) {
			for (EquippableItem offhand : offHands) {
				handleItemOption(mainhand, offhand);
			}
		}
	}

	private void enumerateFingers() {
		List<EquippableItem> rings = getItemVariants(FINGER);

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
		List<EquippableItem> trinkets = getItemVariants(TRINKET);

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
		Set<ItemType> itemTypes = slot.getItemTypes();

		for (EquippableItem item : getItemVariants(itemTypes)) {
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

	private List<EquippableItem> getItemVariants(ItemType itemType) {
		return getItemVariants(Set.of(itemType));
	}

	private List<EquippableItem> getItemVariants(Set<ItemType> itemTypes) {
		List<EquippableItem> result = new ArrayList<>();

		for (ItemType itemType : itemTypes) {
			for (Item item : getItemsToAnalyze(itemType)) {
				result.addAll(getItemVariants(item));
			}
		}

		return result;
	}

	protected abstract List<Item> getItemsToAnalyze(ItemType itemType);

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
