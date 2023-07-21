package wow.character.model.equipment;

import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.item.Gem;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public record ItemSockets(
		ItemSocketSpecification specification,
		List<ItemSocket> sockets
) implements ComplexAttribute, Copyable<ItemSockets>, Iterable<ItemSocket> {
	public static final ItemSockets EMPTY = new ItemSockets(ItemSocketSpecification.EMPTY, List.of());

	public ItemSockets {
		Objects.requireNonNull(specification);
		Objects.requireNonNull(sockets);
	}

	public static ItemSockets create(ItemSocketSpecification specification) {
		if (!specification.hasSockets()) {
			return ItemSockets.EMPTY;
		}

		List<ItemSocket> itemSockets = specification.socketTypes().stream()
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

	@Override
	public ComplexAttributeId id() {
		return ComplexAttributeId.SOCKETS;
	}

	@Override
	public AttributeCondition condition() {
		return AttributeCondition.EMPTY;
	}

	public int getSocketCount() {
		return specification.getSocketCount();
	}

	public SocketType getSocketType(int socketNo) {
		return specification.getSocketType(socketNo);
	}

	public List<SocketType> getSocketTypes() {
		return specification.socketTypes();
	}

	public Attributes getSocketBonus() {
		return specification.socketBonus();
	}

	public boolean allSocketsHaveMatchingGems(int numRed, int numYellow, int numBlue) {
		for (ItemSocket socket : sockets) {
			if (!socket.hasMatchingGem(numRed, numYellow, numBlue)) {
				return false;
			}
		}
		return true;
	}

	public ItemSocket getSocket(int socketNo) {
		if (0 <= socketNo && socketNo < getSocketCount()) {
			return sockets.get(socketNo);
		}
		throw new IllegalArgumentException("Socket no: " + socketNo);
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
			Gem gem = getGem(socketNo);
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

	@Override
	public ItemSockets attachCondition(AttributeCondition condition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<ItemSocket> iterator() {
		return sockets.iterator();
	}

	public Stream<ItemSocket> stream() {
		return sockets.stream();
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
