package wow.minmax.service.impl;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.util.AttributeEvaluator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-08
 */
class GemComboEvaluator {
	private final ItemServiceImpl itemService;

	private final Map<String, List<Gem[]>> casterGemCombosCache = Collections.synchronizedMap(new HashMap<>());

	GemComboEvaluator(ItemServiceImpl itemService) {
		this.itemService = itemService;
	}

	public List<Gem[]> getCasterGemCombos(Item item, Phase phase) {
		ItemSocketsUniqueConfiguration uniqueConfiguration = item.getSocketSpecification().getUniqueConfiguration();
		List<Gem[]> casterGemCombos = getCasterGemCombos(uniqueConfiguration, phase);
		return rearrangeGemsToReflectItemSocketSpecification(uniqueConfiguration, casterGemCombos, item);
	}

	private List<Gem[]> rearrangeGemsToReflectItemSocketSpecification(ItemSocketsUniqueConfiguration uniqueConfiguration, List<Gem[]> gemCombos, Item item) {
		return gemCombos.stream()
				.map(gemCombo -> changeGemOrder(uniqueConfiguration.getSocketSpecification(), gemCombo, item.getSocketSpecification()))
				.collect(Collectors.toList());
	}

	private Gem[] changeGemOrder(ItemSocketSpecification uniqueSpecification, Gem[] gemCombo, ItemSocketSpecification itemSpecification) {
		int socketCount = uniqueSpecification.getSocketCount();

		if (gemCombo.length != socketCount) {
			throw new IllegalArgumentException();
		}

		Gem[] result = new Gem[socketCount];

		for (int i = 1; i <= socketCount; ++i) {
			placeAccordingToSpecification(result, gemCombo[i - 1], uniqueSpecification.getSocketType(i), itemSpecification);
		}

		return result;
	}

	private void placeAccordingToSpecification(Gem[] gemCombo, Gem gem, SocketType socketType, ItemSocketSpecification specification) {
		for (int i = 1; i <= specification.getSocketCount(); ++i) {
			if (specification.getSocketType(i) == socketType && gemCombo[i - 1] == null) {
				gemCombo[i - 1] = gem;
				return;
			}
		}
		throw new IllegalArgumentException("Couldn't allocate socket for " + gem);
	}

	private List<Gem[]> getCasterGemCombos(ItemSocketsUniqueConfiguration uniqueConfiguration, Phase phase) {
		return casterGemCombosCache.computeIfAbsent(
				uniqueConfiguration.getKey() + "#" + phase,
				x -> computeCasterGemCombos(uniqueConfiguration, phase)
		);
	}

	private List<Gem[]> computeCasterGemCombos(ItemSocketsUniqueConfiguration uniqueConfiguration, Phase phase) {
		ItemSocketSpecification specification = uniqueConfiguration.getSocketSpecification();

		List<Gem[]> result = new ArrayList<>();

		if (specification.getSocketCount() == 1) {
			return getSingleGemCombos(phase, specification, result);
		} else if (specification.getSocketCount() == 2) {
			return getDoubleGemCombos(phase, specification, result);
		} else if (specification.getSocketCount() == 3) {
			return getTripleGemCombos(phase, specification, result);
		} else {
			throw new IllegalArgumentException("Socket count should be at most 3 but is " + specification.getSocketCount());
		}
	}

	private List<Gem[]> getSingleGemCombos(Phase phase, ItemSocketSpecification specification, List<Gem[]> result) {
		List<Gem> gems1 = itemService.getAvailableGems(specification, 1, phase, true);
		for (Gem gem1 : gems1) {
			result.add(new Gem[]{gem1});
		}
		return result;
	}

	private List<Gem[]> getDoubleGemCombos(Phase phase, ItemSocketSpecification specification, List<Gem[]> result) {
		List<Gem> gems1 = itemService.getAvailableGems(specification, 1, phase, true);
		List<Gem> gems2 = itemService.getAvailableGems(specification, 2, phase, true);
		for (Gem gem1 : gems1) {
			for (Gem gem2 : gems2) {
				result.add(new Gem[]{gem1, gem2});
			}
		}
		return removeEquivalents2(result, specification);
	}

	private List<Gem[]> getTripleGemCombos(Phase phase, ItemSocketSpecification specification, List<Gem[]> result) {
		List<Gem> gems1 = itemService.getAvailableGems(specification, 1, phase, true);
		List<Gem> gems2 = itemService.getAvailableGems(specification, 2, phase, true);
		List<Gem> gems3 = itemService.getAvailableGems(specification, 3, phase, true);
		for (Gem gem1 : gems1) {
			for (Gem gem2 : gems2) {
				for (Gem gem3 : gems3) {
					result.add(new Gem[]{gem1, gem2, gem3});
				}
			}
		}
		return removeEquivalents3(result, specification);
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

			boolean match = sockets.getSocket(1).gemMatchesSocketColor(gem1) &&
					sockets.getSocket(2).gemMatchesSocketColor(gem2);

			String key = match + "#" + attributes.statString();

			gemEquivalenceGroups.computeIfAbsent(key, x -> new ArrayList<>()).add(gemCombo);
		}

		return gemEquivalenceGroups.values().stream()
				.map(group -> group.get(0))
				.collect(Collectors.toList());
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

			boolean match = sockets.getSocket(1).gemMatchesSocketColor(gem1) &&
					sockets.getSocket(2).gemMatchesSocketColor(gem2) &&
					sockets.getSocket(3).gemMatchesSocketColor(gem3);

			String key = match + "#" + attributes.statString();

			gemEquivalenceGroups.computeIfAbsent(key, x -> new ArrayList<>()).add(gemCombo);
		}

		return gemEquivalenceGroups.values().stream()
				.map(group -> group.get(0))
				.collect(Collectors.toList());
	}
}
