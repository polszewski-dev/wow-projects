package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.repository.ItemDataRepository;
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
	private static final Phase EPIC_GEM_PHASE = Phase.TBC_P3;

	private final ItemDataRepository itemDataRepository;

	private final GemComboEvaluator gemComboEvaluator = new GemComboEvaluator(this);

	private final Map<String, List<Enchant>> casterEnchants = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> coloredGemsByPhase = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> metaGemsByPhase = Collections.synchronizedMap(new HashMap<>());

	@Override
	public Item getItem(int itemId) {
		return itemDataRepository.getItem(itemId).orElseThrow();
	}

	@Override
	public List<Item> getItems() {
		return new ArrayList<>(itemDataRepository.getAllItems());
	}

	@Override
	public List<Item> getItems(Phase phase) {
		return itemDataRepository.getAllItems()
								 .stream()
								 .filter(item -> item.isAvailableDuring(phase))
								 .collect(Collectors.toList());
	}

	@Override
	public List<Item> getItems(Phase phase, ItemSlot slot) {
		return itemDataRepository.getAllItems()
								 .stream()
								 .filter(item -> item.isAvailableDuring(phase) && item.canBeEquippedIn(slot))
								 .collect(Collectors.toList());
	}

	@Override
	public Map<ItemSlot, List<Item>> getItemsBySlot(CharacterInfo characterInfo, Phase phase, SpellSchool spellSchool) {
		var byItemType = itemDataRepository.getCasterItems(characterInfo, phase, spellSchool)
				.stream()
				.filter(item -> isSuitableFor(item, characterInfo))
				.collect(Collectors.groupingBy(Item::getItemType));

		var result = new EnumMap<ItemSlot, List<Item>>(ItemSlot.class);

		for (var entry : byItemType.entrySet()) {
			var itemType = entry.getKey();
			for (ItemSlot itemSlot : itemType.getItemSlots()) {
				var items = byItemType.get(itemType);
				result.computeIfAbsent(itemSlot, x -> new ArrayList<>()).addAll(items);
			}
		}

		return result;
	}

	private boolean isSuitableFor(Item item, CharacterInfo characterInfo) {
		return item.getRestriction().getRequiredLevel() > characterInfo.getLevel() - 10;
	}

	@Override
	public List<Enchant> getAvailableEnchants(Item item, Phase phase) {
		return itemDataRepository.getEnchants(item.getItemType());
	}

	@Override
	public List<Enchant> getCasterEnchants(ItemType itemType, Phase phase, SpellSchool spellSchool) {
		return casterEnchants.computeIfAbsent(itemType + "#" + phase,
				x -> itemDataRepository.getEnchants(itemType)
									   .stream()
									   .filter(enchant -> isCasterEnchant(enchant, itemType, spellSchool))
									   .collect(Collectors.toList())
		);
	}

	private boolean isCasterEnchant(Enchant enchant, ItemType itemType, SpellSchool spellSchool) {
		if (enchant.hasCasterStats(spellSchool)) {
			return true;
		}
		if (itemType == ItemType.CHEST) {
			return enchant.getBaseStatsIncrease() > 0;
		}
		if (itemType == ItemType.BACK) {
			return enchant.getThreatReductionPct().getValue() > 0;
		}
		if (itemType == ItemType.FEET) {
			return enchant.getSpeedIncreasePct().getValue() > 0;
		}
		return false;
	}

	@Override
	public List<Gem> getAvailableGems(Item item, int socketNo, Phase phase, boolean onlyCrafted) {
		return getAvailableGems(item.getSocketSpecification(), socketNo, phase, onlyCrafted);
	}

	List<Gem> getAvailableGems(ItemSocketSpecification specification, int socketNo, Phase phase, boolean onlyCrafted) {
		if (socketNo > specification.getSocketCount()) {
			return Collections.emptyList();//sortable list is needed
		}
		SocketType socketType = specification.getSocketType(socketNo);
		if (socketType == SocketType.META) {
			return getMetaGems(phase, onlyCrafted);
		}
		return getGems(phase, onlyCrafted);
	}

	@Override
	public List<Gem[]> getCasterGemCombos(Item item, Phase phase) {
		return gemComboEvaluator.getCasterGemCombos(item, phase);
	}

	@Override
	public Map<ItemType, List<Item>> getCasterItemsByType(CharacterInfo characterInfo, Phase phase, SpellSchool spellSchool) {
		return itemDataRepository.getCasterItemsByType(characterInfo, phase, spellSchool);
	}

	private List<Gem> getGems(Phase phase, boolean onlyCrafted) {
		return getCachedGems(coloredGemsByPhase, false, phase, onlyCrafted);
	}

	private List<Gem> getMetaGems(Phase phase, boolean onlyCrafted) {
		return getCachedGems(metaGemsByPhase, true, phase, onlyCrafted);
	}

	private List<Gem> getCachedGems(Map<String, List<Gem>> map, boolean meta, Phase phase, boolean onlyCrafted) {
		return map.computeIfAbsent(phase + "#" + onlyCrafted,
				key -> itemDataRepository.getAllGems()
										 .stream()
										 .filter(gem -> gem.isAvailableDuring(phase))
										 .filter(gem -> gem.getRarity().isAtLeastAsGoodAs(getMinimumRarity(meta, phase, onlyCrafted)))
										 .filter(gem -> meta == (gem.getColor() == GemColor.META))
										 .filter(gem -> !onlyCrafted || (gem.isCrafted() && gem.getBinding() != Binding.BINDS_ON_PICK_UP))
										 .filter(Gem::hasCasterStats)
										 .collect(Collectors.toList()));
	}

	private static ItemRarity getMinimumRarity(boolean meta, Phase phase, boolean onlyCrafted) {
		if (meta) {
			return ItemRarity.RARE;
		}
		if (phase.isEarlier(EPIC_GEM_PHASE)) {
			return ItemRarity.RARE;
		}
		return onlyCrafted ? ItemRarity.EPIC : ItemRarity.RARE;
	}
}
