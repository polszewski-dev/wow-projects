package wow.minmax.service.impl.enumerators;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.item.*;
import wow.commons.util.AttributeEvaluator;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.ItemService;
import wow.minmax.service.impl.CachedItemService;
import wow.minmax.service.impl.ItemServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-08
 */
public class GemComboFinder {
	private final ItemService itemService;

	private final Map<String, List<Gem[]>> gemComboCache = Collections.synchronizedMap(new HashMap<>());

	public GemComboFinder(ItemServiceImpl itemService) {
		this.itemService = new CachedItemService(itemService);
	}

	public List<Gem[]> getGemCombos(PlayerProfile playerProfile, ItemSocketSpecification socketSpecification) {
		ItemSocketsUniqueConfiguration uniqueConfiguration = socketSpecification.getUniqueConfiguration();
		List<Gem[]> gemCombos = getGemCombos(playerProfile, uniqueConfiguration);

		return gemCombos.stream()
				.map(gemCombo -> changeBackGemOrder(uniqueConfiguration, gemCombo, socketSpecification))
				.collect(Collectors.toList());
	}

	private List<Gem[]> getGemCombos(PlayerProfile playerProfile, ItemSocketsUniqueConfiguration uniqueConfiguration) {
		return gemComboCache.computeIfAbsent(
				uniqueConfiguration.getKey() + "#" + playerProfile.getPhase(),
				x -> findGemCombos(playerProfile, uniqueConfiguration)
		);
	}

	private List<Gem[]> findGemCombos(PlayerProfile playerProfile, ItemSocketsUniqueConfiguration uniqueConfiguration) {
		ItemSocketSpecification specification = uniqueConfiguration.getSocketSpecification();

		if (specification.getSocketCount() == 1) {
			return getSingleGemCombos(playerProfile, specification);
		} else if (specification.getSocketCount() == 2) {
			return getDoubleGemCombos(playerProfile, specification);
		} else if (specification.getSocketCount() == 3) {
			return getTripleGemCombos(playerProfile, specification);
		} else {
			throw new IllegalArgumentException("Socket count should be at most 3 but is " + specification.getSocketCount());
		}
	}

	private List<Gem[]> getSingleGemCombos(PlayerProfile playerProfile, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getGems(playerProfile, specification.getSocketType(1), true);
		for (Gem gem1 : gems1) {
			result.add(new Gem[]{gem1});
		}
		return result;
	}

	private List<Gem[]> getDoubleGemCombos(PlayerProfile playerProfile, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getGems(playerProfile, specification.getSocketType(1), true);
		List<Gem> gems2 = itemService.getGems(playerProfile, specification.getSocketType(2), true);
		for (Gem gem1 : gems1) {
			for (Gem gem2 : gems2) {
				result.add(new Gem[]{gem1, gem2});
			}
		}
		return removeEquivalents(result, specification);
	}

	private List<Gem[]> getTripleGemCombos(PlayerProfile playerProfile, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getGems(playerProfile, specification.getSocketType(1), true);
		List<Gem> gems2 = itemService.getGems(playerProfile, specification.getSocketType(2), true);
		List<Gem> gems3 = itemService.getGems(playerProfile, specification.getSocketType(3), true);
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