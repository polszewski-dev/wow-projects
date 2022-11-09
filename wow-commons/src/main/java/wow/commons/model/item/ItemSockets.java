package wow.commons.model.item;

import wow.commons.model.Copyable;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public class ItemSockets extends ComplexAttribute implements Copyable<ItemSockets> {
	private final ItemSocketSpecification specification;
	private final List<ItemSocket> sockets;
	private final boolean readOnly;

	public static final ItemSockets EMPTY = new ItemSockets(ItemSocketSpecification.EMPTY, List.of(), true);

	public ItemSockets(ItemSocketSpecification specification, List<ItemSocket> sockets) {
		this(specification, sockets, false);
	}

	public ItemSockets(ItemSocketSpecification specification, List<ItemSocket> sockets, boolean readOnly) {
		super(ComplexAttributeId.SOCKETS);
		this.specification = specification;
		this.sockets = sockets;
		this.readOnly = readOnly;
	}

	@Override
	public ItemSockets copy(boolean readOnly) {
		if (this.readOnly && readOnly) {
			return this;
		}

		return new ItemSockets(
				specification,
				sockets.stream()
					   .map(socket -> socket.copy(readOnly))
					   .collect(Collectors.toList()),
				readOnly
		);
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
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

	@Override
	public AttributeCondition getCondition() {
		return null;
	}

	public boolean allMatch(int numRed, int numYellow, int numBlue) {
		for (int i = 0; i < sockets.size(); i++) {
			ItemSocket socket = sockets.get(i);
			if (!socket.isCompleteMatch(numRed, numYellow, numBlue)) {
				return false;
			}
		}
		return true;
	}

	public ItemSocket getSocket(int socketNo) {
		if (socketNo > getSocketCount()) {
			return null;
		}
		return sockets.get(socketNo - 1);
	}

	public Gem getGem(int socketNo) {
		ItemSocket socket = getSocket(socketNo);
		return socket != null ? socket.getGem() : null;
	}

	public void insertGem(int socketNo, Gem gem) {
		if (socketNo > getSocketCount() && gem == null) {
			return;
		}
		assertCanBeModified();
		sockets.get(socketNo - 1)
			   .insertGem(gem);
	}

	public int getGemCount(SocketType socketType) {
		int result = 0;
		for (int socketNo = 1; socketNo <= getSocketCount(); ++socketNo) {
			Gem gem = getGem(socketNo);
			if (gem != null && gem.getColor().matchesSocket(socketType)) {
				++result;
			}
		}
		return result;
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
