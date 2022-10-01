package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.commons.repository.ItemDataRepository;
import wow.commons.util.AttributeEvaluator;
import wow.minmax.service.ItemService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
	private static final int EPIC_GEM_PHASE = 3;

	private final ItemDataRepository itemDataRepository;

	private final Map<String, List<Gem[]>> casterGemCombosCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> casterEnchants = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> coloredGemsByPhase = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> metaGemsByPhase = Collections.synchronizedMap(new HashMap<>());

	@Override
	public Item getItem(int itemId) {
		Item item = itemDataRepository.getItem(itemId);
		if (item != null) {
			return item;
		}
		throw new IllegalArgumentException("Couldn't find item with id: " + itemId);
	}

	@Override
	public List<Item> getItems() {
		return new ArrayList<>(itemDataRepository.getAllItems());
	}

	@Override
	public List<Item> getItems(int phase) {
		return itemDataRepository.getAllItems()
								 .stream()
								 .filter(item -> itemDataRepository.getPhase(item) <= phase)
								 .collect(Collectors.toList());
	}

	@Override
	public List<Item> getItems(int phase, ItemSlot slot) {
		return itemDataRepository.getAllItems()
								 .stream()
								 .filter(item -> itemDataRepository.getPhase(item) <= phase && item.canBeEquippedIn(slot))
								 .collect(Collectors.toList());
	}

	@Override
	public Map<ItemSlot, List<Item>> getItemsBySlot(int phase, CharacterClass characterClass, SpellSchool spellSchool) {
		var byItemType = itemDataRepository.getCasterItems(phase, characterClass, spellSchool)
										   .stream()
										   .collect(Collectors.groupingBy(Item::getItemType));

		var result = new HashMap<ItemSlot, List<Item>>();

		for (ItemType itemType : byItemType.keySet()) {
			for (ItemSlot itemSlot : itemType.getItemSlots()) {
				result.computeIfAbsent(itemSlot, x -> new ArrayList<>()).addAll(byItemType.get(itemType));
			}
		}

		return result;
	}

	@Override
	public List<Enchant> getAvailableEnchants(Item item, int phase) {
		return itemDataRepository.getEnchants(item.getItemType());
	}

	@Override
	public List<Enchant> getCasterEnchants(ItemType itemType, int phase, SpellSchool spellSchool) {
		return casterEnchants.computeIfAbsent(itemType + "#" + phase,
				x -> itemDataRepository.getEnchants(itemType)
									   .stream()
									   .filter(enchant -> isCasterEnchant(enchant, itemType, spellSchool))
									   .collect(Collectors.toList())
		);
	}

	private boolean isCasterEnchant(Enchant enchant, ItemType itemType, SpellSchool spellSchool) {
		if (itemType == ItemType.Chest) {
			if (enchant.getBaseStatsIncrease() > 0) {
				return true;
			}
		} else if (itemType == ItemType.Back) {
			if (enchant.getThreatReductionPct().getValue() > 0) {
				return true;
			}
		} else if (itemType == ItemType.Feet) {
			if (enchant.getSpeedIncreasePct().getValue() > 0) {
				return true;
			}
		}
		return enchant.hasCasterStats(spellSchool);
	}

	@Override
	public List<Gem> getAvailableGems(Item item, int socketNo, int phase, boolean onlyCrafted) {
		return getAvailableGems(item.getSocketSpecification(), socketNo, phase, onlyCrafted);
	}

	private List<Gem> getAvailableGems(ItemSocketSpecification specification, int socketNo, int phase, boolean onlyCrafted) {
		if (socketNo > specification.getSocketCount()) {
			return Collections.emptyList();//sortable list is needed
		}
		SocketType socketType = specification.getSocketType(socketNo);
		if (socketType == SocketType.Meta) {
			return getMetaGems(phase, onlyCrafted);
		}
		return getGems(phase, onlyCrafted);
	}

	@Override
	public List<Gem[]> getCasterGemCombos(Item item, int phase) {
		ItemSocketsUniqueConfiguration uniqueConfiguration = item.getSocketSpecification().getUniqueConfiguration();
		List<Gem[]> casterGemCombos = getCasterGemCombos(uniqueConfiguration, phase);
		return casterGemCombos.stream()
							  .map(gems -> uniqueConfiguration.getSpecification().changeOrder(item.getSocketSpecification(), gems))
							  .collect(Collectors.toList());
	}

	private List<Gem[]> getCasterGemCombos(ItemSocketsUniqueConfiguration uniqueConfiguration, int phase) {
		return casterGemCombosCache.computeIfAbsent(uniqueConfiguration.getKey() + "#" + phase, x -> {
			ItemSocketSpecification specification = uniqueConfiguration.getSpecification();

			List<Gem[]> result = new ArrayList<>();

			if (specification.getSocketCount() == 1) {
				List<Gem> gems1 = getAvailableGems(specification, 1, phase, true);
				for (Gem gem1 : gems1) {
					result.add(new Gem[]{gem1});
				}
				return result;
			} else if (specification.getSocketCount() == 2) {
				List<Gem> gems1 = getAvailableGems(specification, 1, phase, true);
				List<Gem> gems2 = getAvailableGems(specification, 2, phase, true);
				for (Gem gem1 : gems1) {
					for (Gem gem2 : gems2) {
						result.add(new Gem[]{gem1, gem2});
					}
				}
				return removeEquivalents2(result, specification);
			} else if (specification.getSocketCount() == 3) {
				List<Gem> gems1 = getAvailableGems(specification, 1, phase, true);
				List<Gem> gems2 = getAvailableGems(specification, 2, phase, true);
				List<Gem> gems3 = getAvailableGems(specification, 3, phase, true);
				for (Gem gem1 : gems1) {
					for (Gem gem2 : gems2) {
						for (Gem gem3 : gems3) {
							result.add(new Gem[]{gem1, gem2, gem3});
						}
					}
				}
				return removeEquivalents3(result, specification);
			} else {
				throw new IllegalArgumentException("Socket count should be at most 3 but is " + specification.getSocketCount());
			}
		});
	}

	private List<Gem[]> removeEquivalents2(List<Gem[]> gemCombos, ItemSocketSpecification socketSpecification) {
		Map<String, List<Gem[]>> gemEquivalenceGroups = new HashMap<>();
		ItemSockets sockets = socketSpecification.createSockets();

		for (Gem[] gemCombo : gemCombos) {
			Gem gem1 = gemCombo[0];
			Gem gem2 = gemCombo[1];

			Attributes attributes = AttributeEvaluator.of()
													  .addAttributes(gem1)
													  .addAttributes(gem2)
													  .nothingToSolve()
													  .getAttributes();

			boolean match = sockets.getSocket(1).gemMatchesSocketColor(gem1) && sockets.getSocket(2).gemMatchesSocketColor(gem2);

			String key = match + "#" + attributes.statString();

			gemEquivalenceGroups.computeIfAbsent(key, x -> new ArrayList<>()).add(gemCombo);
		}

		return gemEquivalenceGroups.values().stream().map(group -> group.get(0)).collect(Collectors.toList());
	}

	private List<Gem[]> removeEquivalents3(List<Gem[]> gemCombos, ItemSocketSpecification socketSpecification) {
		Map<String, List<Gem[]>> gemEquivalenceGroups = new HashMap<>();
		ItemSockets sockets = socketSpecification.createSockets();

		for (Gem[] gemCombo : gemCombos) {
			Gem gem1 = gemCombo[0];
			Gem gem2 = gemCombo[1];
			Gem gem3 = gemCombo[2];

			Attributes attributes = AttributeEvaluator.of()
													  .addAttributes(gem1)
													  .addAttributes(gem2)
													  .addAttributes(gem3)
													  .nothingToSolve()
													  .getAttributes();

			boolean match = sockets.getSocket(1).gemMatchesSocketColor(gem1) && sockets.getSocket(2).gemMatchesSocketColor(gem2) && sockets.getSocket(3).gemMatchesSocketColor(gem3);

			String key = match + "#" + attributes.statString();

			gemEquivalenceGroups.computeIfAbsent(key, x -> new ArrayList<>()).add(gemCombo);
		}

		return gemEquivalenceGroups.values().stream().map(group -> group.get(0)).collect(Collectors.toList());
	}

	private List<Gem> getGems(int phase, boolean onlyCrafted) {
		return getCachedGems(coloredGemsByPhase, false, phase, onlyCrafted);
	}

	private List<Gem> getMetaGems(int phase, boolean onlyCrafted) {
		return getCachedGems(metaGemsByPhase, true, phase, onlyCrafted);
	}

	private List<Gem> getCachedGems(Map<String, List<Gem>> map, boolean meta, int phase, boolean onlyCrafted) {
		return map.computeIfAbsent(phase + "#" + onlyCrafted,
				key -> itemDataRepository.getAllGems()
										 .stream()
										 .filter(gem -> gem.getPhase() <= phase)
										 .filter(gem -> gem.getRarity().isAtLeastAsGoodAs(onlyCrafted && phase >= EPIC_GEM_PHASE && !meta ? ItemRarity.Epic : ItemRarity.Rare))
										 .filter(gem -> meta == (gem.getColor() == GemColor.Meta))
										 .filter(gem -> !onlyCrafted || (gem.isCrafted() && gem.getBinding() != Binding.BindsOnPickUp))
										 .filter(Gem::hasCasterStats)
										 .collect(Collectors.toList()));
	}
}
