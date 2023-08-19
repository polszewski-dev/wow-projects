package wow.simulator.model.update;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import wow.simulator.model.time.Time;

import java.util.Comparator;

/**
 * User: POlszewski
 * Date: 2023-08-18
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Handle<T extends Updateable> {
	private final T element;
	private long order;

	public T get() {
		return element;
	}

	long getOrder() {
		return order;
	}

	void refreshOrder(long order) {
		this.order = order;
	}

	Time getNextUpdateTime() {
		return element.getNextUpdateTime().orElseThrow();
	}

	public static <T extends Updateable> Comparator<Handle<T>> orderComparator() {
		return Comparator.comparingLong(Handle::getOrder);
	}
}
