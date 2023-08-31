package wow.commons.model.item;

import wow.commons.model.attribute.Attributes;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record ItemSocketSpecification(List<SocketType> socketTypes, Attributes socketBonus) {
	public static final ItemSocketSpecification EMPTY = new ItemSocketSpecification(List.of(), Attributes.EMPTY);

	public ItemSocketSpecification {
		Objects.requireNonNull(socketBonus);
		Objects.requireNonNull(socketBonus);
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

	@Override
	public List<SocketType> socketTypes() {
		return Collections.unmodifiableList(socketTypes);
	}


	@Override
	public String toString() {
		return String.format("%s, bonus: %s", socketTypes, socketBonus);
	}
}
