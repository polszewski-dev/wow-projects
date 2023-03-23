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
public class ItemSockets extends ComplexAttribute implements Copyable<ItemSockets>, Iterable<ItemSocket> {
	private final ItemSocketSpecification specification;
	private final List<ItemSocket> sockets;

	public static final ItemSockets EMPTY = new ItemSockets(ItemSocketSpecification.EMPTY, List.of());

	private ItemSockets(ItemSocketSpecification specification, List<ItemSocket> sockets) {
		super(ComplexAttributeId.SOCKETS, AttributeCondition.EMPTY);
		this.specification = specification;
		this.sockets = sockets;
	}

	public static ItemSockets create(ItemSocketSpecification specification) {
		if (!specification.hasSockets()) {
			return ItemSockets.EMPTY;
		}

		List<ItemSocket> itemSockets = specification.getSocketTypes().stream()
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

	public List<SocketType> getSocketTypes() {
		return specification.getSocketTypes();
	}

	public Attributes getSocketBonus() {
		return specification.getSocketBonus();
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ItemSockets)) return false;
		ItemSockets that = (ItemSockets) o;
		return specification.equals(that.specification) &&
				sockets.equals(that.sockets);
	}

	@Override
	public int hashCode() {
		return Objects.hash(specification, sockets);
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
