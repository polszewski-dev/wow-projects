package wow.minmax.service.impl.enumerator;

import lombok.AllArgsConstructor;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Gem;
import wow.commons.model.item.ItemSocketSpecification;
import wow.minmax.model.Player;
import wow.minmax.service.ItemService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-08
 */
@AllArgsConstructor
public class GemComboFinder {
	private final Player player;
	private final ItemSocketSpecification socketSpecification;
	private final ItemSockets itemSockets;
	private final ItemSlotGroup slotGroup;
	private final boolean includeUniqueGems;
	private final ItemService itemService;

	public GemComboFinder(
			Player player,
			ItemSocketSpecification socketSpecification,
			ItemSlotGroup slotGroup,
			boolean includeUniqueGems,
			ItemService itemService
	) {
		this.player = player;
		this.socketSpecification = socketSpecification;
		this.itemSockets = ItemSockets.create(socketSpecification);
		this.slotGroup = slotGroup;
		this.includeUniqueGems = includeUniqueGems;
		this.itemService = itemService;
	}

	public List<Gem[]> getGemCombos() {
		return switch (socketSpecification.getSocketCount()) {
			case 1 -> getSingleGemCombos();
			case 2 -> getDoubleGemCombos();
			case 3 -> getTripleGemCombos();
			default ->
				throw new IllegalArgumentException("Socket count should be at most 3 but is " + socketSpecification.getSocketCount());
		};
	}

	private List<Gem[]> getSingleGemCombos() {
		var result = new ArrayList<Gem[]>();
		int socketNo = 0;
		var gems1 = getBestGems(socketNo);
		for (var gem1 : gems1) {
			result.add(new Gem[]{gem1});
		}
		return result;
	}

	private List<Gem[]> getDoubleGemCombos() {
		var result = new ArrayList<Gem[]>();
		var gems1 = getBestGems(0);
		var gems2 = getBestGems(1);
		for (var gem1 : gems1) {
			for (var gem2 : gems2) {
				if (!uniquenessIsViolated(gem1, gem2)) {
					result.add(new Gem[]{gem1, gem2});
				}
			}
		}
		return removeEquivalents(result);
	}

	private List<Gem[]> getTripleGemCombos() {
		var result = new ArrayList<Gem[]>();
		var gems1 = getBestGems(0);
		var gems2 = getBestGems(1);
		var gems3 = getBestGems(2);
		for (var gem1 : gems1) {
			for (var gem2 : gems2) {
				for (var gem3 : gems3) {
					if (!uniquenessIsViolated(gem1, gem2, gem3)) {
						result.add(new Gem[]{gem1, gem2, gem3});
					}
				}
			}
		}
		return removeEquivalents(result);
	}

	private List<Gem> getBestGems(int socketNo) {
		var socketType = socketSpecification.getSocketType(socketNo);
		var nonUniqueGems = itemService.getBestNonUniqueGems(player, socketType);

		if (!includeUniqueGems) {
			return nonUniqueGems;
		}

		var uniqueGems = itemService.getGems(player, socketType, true);
		
		uniqueGems = filterOutAlreadyEquipped(uniqueGems);

		if (uniqueGems.isEmpty()) {
			return nonUniqueGems;
		}

		var combined = new ArrayList<>(nonUniqueGems);
		combined.addAll(uniqueGems);
		return new FilterOutWorseGemChoices(combined).getResult();
	}

	private List<Gem> filterOutAlreadyEquipped(List<Gem> uniqueGems) {
		if (uniqueGems.isEmpty()) {
			return uniqueGems;
		}

		var result = new ArrayList<>(uniqueGems);
		var equippedUniqueGems = getEquippedUniqueGems();

		result.removeAll(equippedUniqueGems);
		return result;
	}

	private List<Gem> getEquippedUniqueGems() {
		var result = new ArrayList<Gem>();

		for (var entry : player.getEquipment().toMap().entrySet()) {
			var slot = entry.getKey();

			if (slotGroup.getSlots().contains(slot)) {
				continue;
			}

			var item = entry.getValue();

			for (var gem : item.getGems()) {
				if (gem.isEffectivelyUnique()) {
					result.add(gem);
				}
			}
		}

		return result;
	}

	private List<Gem[]> removeEquivalents(List<Gem[]> gemCombos) {
		var gemEquivalenceGroups = new LinkedHashMap<String, List<Gem[]>>();

		for (var gemCombo : gemCombos) {
			var key = getKey(gemCombo);

			gemEquivalenceGroups.computeIfAbsent(key, x -> new ArrayList<>()).add(gemCombo);
		}

		return gemEquivalenceGroups.values().stream()
				.map(this::pickTheBestEquivalent)
				.toList();
	}

	private Gem[] pickTheBestEquivalent(List<Gem[]> group) {
		return group.stream()
				.sorted(equivalentComparator)
				.findAny()
				.orElseThrow();
	}

	private Comparator<Gem[]> equivalentComparator = Comparator
			.comparingInt((Gem[] gems) -> matchesSockets(gems) ? 0 : 1)
			.thenComparingInt(this::getUniqueGemCount)
			.thenComparingInt(gems -> -getMatchingGemCount(gems));

	private boolean matchesSockets(Gem[] gems) {
		return itemSockets.matchesSockets(gems);
	}

	private int getUniqueGemCount(Gem[] gems) {
		int count = 0;
		for (var gem : gems) {
			if (gem.isEffectivelyUnique()) {
				++count;
			}
		}
		return count;
	}

	private int getMatchingGemCount(Gem[] gems) {
		int count = 0;
		for (int i = 0; i < gems.length; i++) {
			if (itemSockets.getSocket(i).matchesSocketColor(gems[i])) {
				++count;
			}
		}
		return count;
	}

	private String getKey(Gem[] gemCombo) {
		return Stream.of(gemCombo)
				.mapToInt(Gem::getId)
				.sorted()
				.mapToObj(String::valueOf)
				.collect(Collectors.joining(","));
	}

	private boolean uniquenessIsViolated(Gem gem1, Gem gem2) {
		return gem1.isEffectivelyUnique() && gem2 == gem1;
	}

	private boolean uniquenessIsViolated(Gem gem1, Gem gem2, Gem gem3) {
		return uniquenessIsViolated(gem1, gem2) || uniquenessIsViolated(gem1, gem3) || uniquenessIsViolated(gem2, gem3);
	}
}
