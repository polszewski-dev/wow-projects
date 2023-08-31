package wow.character.util;

import lombok.RequiredArgsConstructor;
import wow.character.model.character.Character;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemColor;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.SocketType;

import java.util.ArrayList;
import java.util.List;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
@RequiredArgsConstructor
public abstract class AbstractEffectCollector implements EffectCollector {
	protected final Character character;
	private ItemSockets metaSockets;
	private final List<ItemSet> itemSets = new ArrayList<>();
	private int numRed;
	private int numYellow;
	private int numBlue;

	public void collectEffects() {
		character.collectEffects(this);
	}

	public void collectRest() {
		solveItemSockets();
		solveItemSets();
	}

	public void solveAll() {
		collectEffects();
		collectRest();
	}

	private void solveItemSockets() {
		if (metaSockets != null) {
			solveItemSockets(metaSockets);
		}
	}

	private void solveItemSets() {
		if (itemSets.isEmpty()) {
			return;
		}

		var itemSetPieces = itemSets
				.stream()
				.collect(groupingBy(identity(), counting()));

		for (var entry : itemSetPieces.entrySet()) {
			var itemSet = entry.getKey();
			var numPieces = entry.getValue();

			for (var itemSetBonus : itemSet.getItemSetBonuses()) {
				if (numPieces >= itemSetBonus.numPieces()) {
					addEffect(itemSetBonus.bonusEffect());
				}
			}
		}
	}

	@Override
	public void addItemSockets(ItemSockets itemSockets) {
		if (itemSockets.hasMetaSocket()) {
			addMetaSockets(itemSockets);
		} else {
			solveItemSockets(itemSockets);
		}
	}

	private void addMetaSockets(ItemSockets itemSockets) {
		if (metaSockets != null) {
			throw new IllegalArgumentException("Two meta sockets");
		}
		metaSockets = itemSockets;
	}

	private void solveItemSockets(ItemSockets itemSockets) {
		numRed += itemSockets.getGemCount(SocketType.RED);
		numYellow += itemSockets.getGemCount(SocketType.YELLOW);
		numBlue += itemSockets.getGemCount(SocketType.BLUE);

		for (int i = 0; i < itemSockets.getSocketCount(); ++i) {
			Gem gem = itemSockets.getGem(i);
			if (gem != null && (gem.getColor() != GemColor.META || gem.isMetaConditionTrue(numRed, numYellow, numBlue))) {
				addEffects(gem.getEffects());
			}
		}

		if (itemSockets.allSocketsHaveMatchingGems(numRed, numYellow, numBlue)) {
			addEffect(itemSockets.getSocketBonus());
		}
	}

	@Override
	public void addItemSet(ItemSet itemSet) {
		var requiredProfession = itemSet.getRequiredProfession();

		if (requiredProfession == null || character.hasProfession(requiredProfession)) {
			itemSets.add(itemSet);
		}
	}

	protected void copyFieldsFrom(AbstractEffectCollector collector) {
		this.metaSockets = collector.metaSockets;
		this.itemSets.addAll(collector.itemSets);
		this.numRed = collector.numRed;
		this.numYellow = collector.numYellow;
		this.numBlue = collector.numBlue;
	}
}
