package wow.commons.model.item;

import wow.commons.model.attributes.Attributes;

import java.util.Collections;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public class ItemSocketSpecification {
	private final List<SocketType> socketTypes;
	private final Attributes socketBonus;

	public static final ItemSocketSpecification EMPTY = new ItemSocketSpecification(List.of(), Attributes.EMPTY);

	public ItemSocketSpecification(List<SocketType> socketTypes, Attributes socketBonus) {
		this.socketTypes = socketTypes;
		this.socketBonus = socketBonus;
	}

	public int getSocketCount() {
		return socketTypes.size();
	}

	public boolean hasSockets() {
		return getSocketCount() > 0;
	}

	public SocketType getSocketType(int socketNo) {
		return socketTypes.get(socketNo);
	}

	public List<SocketType> getSocketTypes() {
		return Collections.unmodifiableList(socketTypes);
	}

	public Attributes getSocketBonus() {
		return socketBonus;
	}
}
