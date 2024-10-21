package wow.commons.model.item;

import wow.commons.model.effect.Effect;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record ItemSocketSpecification(List<SocketType> socketTypes, Effect socketBonus) {
	public static final ItemSocketSpecification EMPTY = new ItemSocketSpecification(
			List.of(), Effect.EMPTY
	);

	public ItemSocketSpecification {
		Objects.requireNonNull(socketBonus);
		socketTypes = List.copyOf(socketTypes);
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

	public boolean hasMetaSocket() {
		return socketTypes.contains(SocketType.META);
	}

	@Override
	public String toString() {
		return String.format("%s, bonus: %s", socketTypes, socketBonus.getTooltip());
	}
}
