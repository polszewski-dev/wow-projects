package wow.character.model.equipment;

import wow.character.model.Copyable;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Gem;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public class ItemSockets implements Copyable<ItemSockets>, Iterable<ItemSocket> {
	private final ItemSocketSpecification specification;
	private final List<ItemSocket> sockets;

	private ItemSockets(ItemSocketSpecification specification, List<ItemSocket> sockets) {
		this.specification = specification;
		this.sockets = sockets;
	}

	public static ItemSockets create(ItemSocketSpecification specification) {
		var itemSockets = specification.socketTypes().stream()
				.map(ItemSocket::new)
				.toList();

		return new ItemSockets(specification, itemSockets);
	}

	@Override
	public ItemSockets copy() {
		return new ItemSockets(
				specification,
				sockets.stream()
					   .map(ItemSocket::copy)
					   .toList()
		);
	}

	public int getSocketCount() {
		return specification.getSocketCount();
	}

	public SocketType getSocketType(int socketNo) {
		return specification.getSocketType(socketNo);
	}

	public Effect getSocketBonus() {
		return specification.socketBonus();
	}

	public boolean allSocketsHaveMatchingGems(int numRed, int numYellow, int numBlue) {
		for (var socket : sockets) {
			if (!socket.hasMatchingGem(numRed, numYellow, numBlue)) {
				return false;
			}
		}

		return true;
	}

	public ItemSocket getSocket(int socketNo) {
		return sockets.get(socketNo);
	}

	public List<Gem> getGems() {
		return sockets.stream()
				.map(ItemSocket::getGem)
				.toList();
	}

	public Gem getGem(int socketNo) {
		return getSocket(socketNo).getGem();
	}

	public void insertGem(int socketNo, Gem gem) {
		getSocket(socketNo).insertGem(gem);
	}

	public int getGemCount(SocketType socketType) {
		int result = 0;

		for (int socketNo = 0; socketNo < getSocketCount(); ++socketNo) {
			var gem = getGem(socketNo);

			if (gem != null && gem.getColor().matchesSocket(socketType)) {
				++result;
			}
		}

		return result;
	}

	public boolean matchesSockets(Gem[] gemCombo) {
		for (int i = 0; i < getSocketCount(); ++i) {
			if (!getSocket(i).matchesSocketColor(gemCombo[i])) {
				return false;
			}
		}

		return true;
	}

	public boolean hasMetaSocket() {
		return specification.hasMetaSocket();
	}

	@Override
	public Iterator<ItemSocket> iterator() {
		return sockets.iterator();
	}

	@Override
	public String toString() {
		if (getSocketCount() == 0) {
			return "no sockets";
		}

		return String.format(
				"%s, socket bonus: %s",
				sockets.stream()
					   .map(Object::toString)
					   .collect(Collectors.joining("")),
				getSocketBonus()
		);
	}
}
