package wow.character.service.impl.enumerators;

import lombok.AllArgsConstructor;
import wow.character.model.character.Character;
import wow.character.model.equipment.ItemSockets;
import wow.character.service.ItemService;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.item.Gem;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-08
 */
@AllArgsConstructor
public class GemComboFinder {
	private final ItemService itemService;

	public List<Gem[]> getGemCombos(Character character, ItemSocketSpecification socketSpecification) {
		ItemSocketsUniqueConfiguration uniqueConfiguration = ItemSocketsUniqueConfiguration.of(socketSpecification.getSocketTypes());
		List<Gem[]> gemCombos = getGemCombos(character, uniqueConfiguration);

		return gemCombos.stream()
				.map(gemCombo -> changeBackGemOrder(uniqueConfiguration, gemCombo, socketSpecification))
				.collect(Collectors.toList());
	}

	private List<Gem[]> getGemCombos(Character character, ItemSocketsUniqueConfiguration uniqueConfiguration) {
		ItemSocketSpecification specification = uniqueConfiguration.getSpecification();

		if (specification.getSocketCount() == 1) {
			return getSingleGemCombos(character, specification);
		} else if (specification.getSocketCount() == 2) {
			return getDoubleGemCombos(character, specification);
		} else if (specification.getSocketCount() == 3) {
			return getTripleGemCombos(character, specification);
		} else {
			throw new IllegalArgumentException("Socket count should be at most 3 but is " + specification.getSocketCount());
		}
	}

	private List<Gem[]> getSingleGemCombos(Character character, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getBestGems(character, specification.getSocketType(0));
		for (Gem gem1 : gems1) {
			result.add(new Gem[]{gem1});
		}
		return result;
	}

	private List<Gem[]> getDoubleGemCombos(Character character, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getBestGems(character, specification.getSocketType(0));
		List<Gem> gems2 = itemService.getBestGems(character, specification.getSocketType(1));
		for (Gem gem1 : gems1) {
			for (Gem gem2 : gems2) {
				result.add(new Gem[]{gem1, gem2});
			}
		}
		return removeEquivalents(result, specification);
	}

	private List<Gem[]> getTripleGemCombos(Character character, ItemSocketSpecification specification) {
		List<Gem[]> result = new ArrayList<>();
		List<Gem> gems1 = itemService.getBestGems(character, specification.getSocketType(0));
		List<Gem> gems2 = itemService.getBestGems(character, specification.getSocketType(1));
		List<Gem> gems3 = itemService.getBestGems(character, specification.getSocketType(2));
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
		ItemSockets sockets = ItemSockets.create(socketSpecification);

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
		ItemSocketSpecification uniqueSpecification = uniqueConfiguration.getSpecification();
		int socketCount = uniqueSpecification.getSocketCount();

		if (gemCombo.length != socketCount) {
			throw new IllegalArgumentException();
		}

		Gem[] result = new Gem[socketCount];

		for (int i = 0; i < socketCount; ++i) {
			placeAccordingToSpecification(result, gemCombo[i], uniqueSpecification.getSocketType(i), itemSpecification);
		}

		return result;
	}

	private void placeAccordingToSpecification(Gem[] gemCombo, Gem gem, SocketType socketType, ItemSocketSpecification specification) {
		for (int i = 0; i < specification.getSocketCount(); ++i) {
			if (specification.getSocketType(i) == socketType && gemCombo[i] == null) {
				gemCombo[i] = gem;
				return;
			}
		}
		throw new IllegalArgumentException("Couldn't allocate socket for " + gem);
	}
}
