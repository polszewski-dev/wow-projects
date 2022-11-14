package wow.minmax.service.impl.enumerators;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.util.AttributeEvaluator;
import wow.minmax.model.PVERole;
import wow.minmax.service.impl.ItemServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-08
 */
public class GemComboEvaluator {
	private final ItemServiceImpl itemService;

	private final Map<String, List<Gem[]>> gemComboCache = Collections.synchronizedMap(new HashMap<>());

	public GemComboEvaluator(ItemServiceImpl itemService) {
		this.itemService = itemService;
	}

	public List<Gem[]> getGemCombos(PVERole role, Phase phase, ItemSocketSpecification socketSpecification) {
		ItemSocketsUniqueConfiguration uniqueConfiguration = socketSpecification.getUniqueConfiguration();
		List<Gem[]> gemCombos = getGemCombos(role, phase, uniqueConfiguration);

		return gemCombos.stream()
				.map(gemCombo -> changeBackGemOrder(uniqueConfiguration, gemCombo, socketSpecification))
				.collect(Collectors.toList());
	}

	private List<Gem[]> getGemCombos(PVERole role, Phase phase, ItemSocketsUniqueConfiguration uniqueConfiguration) {
		return gemComboCache.computeIfAbsent(
				uniqueConfiguration.getKey() + "#" + phase,
				x -> findGemCombos(role, phase, uniqueConfiguration)
		);
	}

	private List<Gem[]> findGemCombos(PVERole role, Phase phase, ItemSocketsUniqueConfiguration uniqueConfiguration) {
		ItemSocketSpecification specification = uniqueConfiguration.getSocketSpecification();

		if (specification.getSocketCount() == 1) {
			return getSingleGemCombos(role, phase, specification);
		} else if (specification.getSocketCount() == 2) {
			return getDoubleGemCombos(role, phase, specification);
		} else if (specification.getSocketCount() == 3) {
			return getTripleGemCombos(role, phase, specification);
		} else {
			throw new IllegalArgumentException("Socket count should be at most 3 but is " + specification.getSocketCount());
		}
	}

	private List<Gem[]> getSingleGemCombos(PVERole role, Phase phase, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getAvailableGems(role, phase, specification.getSocketType(1), true);
		for (Gem gem1 : gems1) {
			result.add(new Gem[]{gem1});
		}
		return result;
	}

	private List<Gem[]> getDoubleGemCombos(PVERole role, Phase phase, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getAvailableGems(role, phase, specification.getSocketType(1), true);
		List<Gem> gems2 = itemService.getAvailableGems(role, phase, specification.getSocketType(2), true);
		for (Gem gem1 : gems1) {
			for (Gem gem2 : gems2) {
				result.add(new Gem[]{gem1, gem2});
			}
		}
		return removeEquivalents(result, specification);
	}

	private List<Gem[]> getTripleGemCombos(PVERole role, Phase phase, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getAvailableGems(role, phase, specification.getSocketType(1), true);
		List<Gem> gems2 = itemService.getAvailableGems(role, phase, specification.getSocketType(2), true);
		List<Gem> gems3 = itemService.getAvailableGems(role, phase, specification.getSocketType(3), true);
		for (Gem gem1 : gems1) {
			for (Gem gem2 : gems2) {
				for (Gem gem3 : gems3) {
					result.add(new Gem[]{gem1, gem2, gem3});
				}
			}
		}
		return removeEquivalents(result, specification);
	}

	private List<Gem[]> removeEquivalents(List<Gem[]> gemCombos, ItemSocketSpecification socketSpecification) {
		Map<String, List<Gem[]>> gemEquivalenceGroups = new HashMap<>();
		ItemSockets sockets = socketSpecification.createSockets();

		for (Gem[] gemCombo : gemCombos) {
			String key = getKey(gemCombo, sockets);

			gemEquivalenceGroups.computeIfAbsent(key, x -> new ArrayList<>()).add(gemCombo);
		}

		return gemEquivalenceGroups.values().stream()
				.map(group -> group.get(0))
				.collect(Collectors.toList());
	}

	private String getKey(Gem[] gemCombo, ItemSockets sockets) {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(List.of(gemCombo))
				.nothingToSolve();

		boolean match = sockets.matchesSockets(gemCombo);

		return match + "#" + attributes.statString();
	}

	private Gem[] changeBackGemOrder(ItemSocketsUniqueConfiguration uniqueConfiguration, Gem[] gemCombo, ItemSocketSpecification itemSpecification) {
		ItemSocketSpecification uniqueSpecification = uniqueConfiguration.getSocketSpecification();
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
}
