package wow.minmax.service.impl;

import wow.commons.model.Percent;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.service.CalculationService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.categorization.ItemType.Finger;
import static wow.commons.model.categorization.ItemType.Trinket;

/**
 * User: POlszewski
 * Date: 2022-01-01
 */
abstract class ItemCombinator<T extends ItemCombinator<T>> {
	protected final PlayerProfile playerProfileOriginal;
	protected final PlayerProfile playerProfile;
	protected final Spell spell;
	protected final Map<ItemSlot, List<EquippableItem>> itemsBySlot = new EnumMap<>(ItemSlot.class);
	protected final List<Equipment> combinations = new ArrayList<>();

	private final CalculationService calculationService;
	private final ItemDataRepository itemDataRepository;

	protected ItemCombinator(PlayerProfile playerProfile, Spell spell,
							 CalculationService calculationService, ItemDataRepository itemDataRepository) {
		this.playerProfileOriginal = playerProfile.readOnlyCopy();
		this.playerProfile = playerProfile.copy();
		this.spell = spell;
		this.calculationService = calculationService;
		this.itemDataRepository = itemDataRepository;
	}

	public T execute() {
		prepareItemVariants();
		combineItems();
		return (T) this;
	}

	protected abstract void prepareItemVariants();

	protected abstract void checkCombination();

	public List<Equipment> getCombinations() {
		return combinations;
	}

	public List<Comparison> getComparisons() {
		return combinations.stream()
						   .map(equipment -> getComparison(playerProfileOriginal, equipment, spell))
						   .collect(Collectors.toList());
	}

	private Comparison getComparison(PlayerProfile playerProfile, Equipment possibleEquipment, Spell spell) {
		PlayerProfile playerProfileCopy = playerProfile.copy();

		double referenceGrade = calculationService.getSpellStatistics(playerProfileCopy, spell).dps;

		playerProfileCopy.setEquipment(possibleEquipment);
		double possibleGrade = calculationService.getSpellStatistics(playerProfileCopy, spell).dps;

		double changePct = 100.0 * possibleGrade / referenceGrade - 100;

		return new Comparison(possibleEquipment, playerProfile.getEquipment(), Percent.of(changePct));
	}

	public Comparison getBestComparison() {
		return getComparisons().stream()
							   .max(Comparator.comparing(x -> x.changePct))
							   .orElse(null);
	}

	private void combineItems() {
		List<EquippableItem> EMPTY = Collections.singletonList(null);

		for (var head : itemsBySlot.getOrDefault(ItemSlot.Head, EMPTY)) {
			playerProfile.getEquipment()
						 .set(head);
			for (var neck : itemsBySlot.getOrDefault(ItemSlot.Neck, EMPTY)) {
				playerProfile.getEquipment()
							 .set(neck);
				for (var shoulder : itemsBySlot.getOrDefault(ItemSlot.Shoulder, EMPTY)) {
					playerProfile.getEquipment()
								 .set(shoulder);
					for (var back : itemsBySlot.getOrDefault(ItemSlot.Back, EMPTY)) {
						playerProfile.getEquipment()
									 .set(back);
						for (var chest : itemsBySlot.getOrDefault(ItemSlot.Chest, EMPTY)) {
							playerProfile.getEquipment()
										 .set(chest);
							for (var wrist : itemsBySlot.getOrDefault(ItemSlot.Wrist, EMPTY)) {
								playerProfile.getEquipment()
											 .set(wrist);
								for (var hands : itemsBySlot.getOrDefault(ItemSlot.Hands, EMPTY)) {
									playerProfile.getEquipment()
												 .set(hands);
									for (var waist : itemsBySlot.getOrDefault(ItemSlot.Waist, EMPTY)) {
										playerProfile.getEquipment()
													 .set(waist);
										for (var legs : itemsBySlot.getOrDefault(ItemSlot.Legs, EMPTY)) {
											playerProfile.getEquipment()
														 .set(legs);
											for (var feet : itemsBySlot.getOrDefault(ItemSlot.Feet, EMPTY)) {
												playerProfile.getEquipment()
															 .set(feet);

												List<EquippableItem> rings = itemsBySlot.getOrDefault(ItemSlot.Finger1, EMPTY);

												for (int i1 = 0; i1 < rings.size(); i1++) {
													EquippableItem finger1 = rings.get(i1);
													playerProfile.getEquipment()
																 .set(finger1, ItemSlot.Finger1);
													for (int i2 = i1 + 1; i2 < rings.size(); i2++) {
														EquippableItem finger2 = rings.get(i2);
														playerProfile.getEquipment()
																	 .set(finger2, ItemSlot.Finger2);

														List<EquippableItem> trinkets = itemsBySlot.getOrDefault(ItemSlot.Trinket1, EMPTY);

														for (int i3 = 0; i3 < trinkets.size(); i3++) {
															EquippableItem trinket1 = trinkets.get(i3);
															playerProfile.getEquipment()
																		 .set(trinket1, ItemSlot.Trinket1);

															for (int i4 = i3 + 1; i4 < trinkets.size(); i4++) {
																EquippableItem trinket2 = trinkets.get(i4);
																playerProfile.getEquipment()
																			 .set(trinket2, ItemSlot.Trinket2);

																for (var mainHand : itemsBySlot.getOrDefault(ItemSlot.MainHand, EMPTY)) {
																	playerProfile.getEquipment()
																				 .set(mainHand);
																	for (var offHand : itemsBySlot.getOrDefault(ItemSlot.OffHand, EMPTY)) {
																		playerProfile.getEquipment()
																					 .set(offHand);
																		for (var ranged : itemsBySlot.getOrDefault(ItemSlot.Ranged, EMPTY)) {
																			playerProfile.getEquipment()
																						 .set(ranged);
																			checkCombination();
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected void addCurrentlyEquippedItems() {
		for (ItemSlot slot : new ItemSlot[]{
				ItemSlot.Head,
				ItemSlot.Neck,
				ItemSlot.Shoulder,
				ItemSlot.Back,
				ItemSlot.Chest,
				ItemSlot.Wrist,
				ItemSlot.Hands,
				ItemSlot.Waist,
				ItemSlot.Legs,
				ItemSlot.Feet,
				ItemSlot.Finger1,
				ItemSlot.Trinket1,
				ItemSlot.MainHand,
				ItemSlot.OffHand,
				ItemSlot.Ranged
		}) {
			Equipment equipment = playerProfile.getEquipment();
			if (slot == ItemSlot.Finger1) {
				addItem(equipment.get(ItemSlot.Finger1));
				addItem(equipment.get(ItemSlot.Finger2));
			} else if (slot == ItemSlot.Trinket1) {
				addItem(equipment.get(ItemSlot.Trinket1));
				addItem(equipment.get(ItemSlot.Trinket2));
			} else {
				addItem(equipment.get(slot));
			}
		}
	}

	protected void addItems(String[] itemNames, String[] enchantNames, String[] gemNames) {
		if (enchantNames == null) {
			enchantNames = new String[]{};
		}
		if (enchantNames.length == 0) {
			enchantNames = new String[]{null};
		}
		if (gemNames == null) {
			gemNames = new String[]{};
		}
		for (String itemName : itemNames) {
			Item item = itemDataRepository.getItem(itemName);
			if (item == null) {
				throw new IllegalArgumentException("No item: " + itemName);
			}
			for (String enchantName : enchantNames) {
				EquippableItem equippableItem = new EquippableItem(item);

				if (enchantName != null) {
					Enchant enchant = itemDataRepository.getEnchant(enchantName);
					if (enchant == null) {
						throw new IllegalArgumentException("No enchant: " + enchantName);
					}
					equippableItem.enchant(enchant);
				}

				if (gemNames.length != 0) {
					Gem[] gems = Stream.of(gemNames)
							.map(itemDataRepository::getGem)
							.toArray(Gem[]::new);

					if (Stream.of(gems).anyMatch(Objects::isNull) || gems.length != equippableItem.getSocketCount()) {
						throw new IllegalArgumentException("No gem!!!");
					}

					equippableItem.gem(gems);
				}

				addItem(equippableItem);
			}
		}
	}

	protected void addItem(EquippableItem item) {
		if (item == null) {
			return;
		}
		ItemSlot slot;
		if (item.getItemType() == Finger) {
			slot = ItemSlot.Finger1;
		} else if (item.getItemType() == Trinket) {
			slot = ItemSlot.Trinket1;
		} else {
			slot = item.getItemType()
					   .getUniqueItemSlot();
		}
		itemsBySlot.computeIfAbsent(slot, x -> new ArrayList<>())
				   .add(item.readOnlyCopy());
	}
}
