package wow.commons.model.item;

import wow.commons.model.attributes.Attributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public class ItemSocketSpecification {
	private final List<SocketType> socketTypes;
	private final Attributes socketBonus;
	private final ItemSocketsUniqueConfiguration uniqueConfiguration;

	public static final ItemSocketSpecification EMPTY = new ItemSocketSpecification(List.of(), Attributes.EMPTY);

	public ItemSocketSpecification(List<SocketType> socketTypes, Attributes socketBonus) {
		this.socketTypes = socketTypes;
		this.socketBonus = socketBonus;
		this.uniqueConfiguration = ItemSocketsUniqueConfiguration.of(socketTypes);
	}

	public int getSocketCount() {
		return socketTypes.size();
	}

	public boolean hasSockets() {
		return getSocketCount() > 0;
	}

	public SocketType getSocketType(int socketNo) {
		if (socketNo > getSocketCount()) {
			return null;
		}
		return socketTypes.get(socketNo - 1);
	}

	public List<SocketType> getSocketTypes() {
		return Collections.unmodifiableList(socketTypes);
	}

	public Attributes getSocketBonus() {
		return socketBonus;
	}

	public ItemSocketsUniqueConfiguration getUniqueConfiguration() {
		return uniqueConfiguration;
	}

	public ItemSockets createSockets() {
		if (!hasSockets()) {
			return ItemSockets.EMPTY;
		}

		List<ItemSocket> itemSockets = socketTypes.stream()
												  .map(ItemSocket::new)
												  .collect(Collectors.toUnmodifiableList());
		return new ItemSockets(this, itemSockets);
	}
}
