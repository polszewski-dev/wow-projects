package wow.simulator.model.update;

import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.TimeAware;

import java.util.*;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
public class UpdateQueue<T extends Updateable> implements TimeAware {
	private final PriorityQueue<Handle<T>> queue = new PriorityQueue<>(Comparator
			.comparing(Handle<T>::getNextUpdateTime)
			.thenComparingLong(Handle::getOrder)
	);
	private long orderGenerator;
	private Clock clock;

	public Handle<T> add(T updateable) {
		updateable.onAddedToQueue();
		if (updateable.getNextUpdateTime().isEmpty()) {
			throw new IllegalArgumentException();
		}
		var handle = new Handle<>(updateable, newOrder());
		queue.add(handle);
		return handle;
	}

	public void updateAllPresentActions() {
		for (Handle<T> handle; (handle = queue.peek()) != null && handle.getNextUpdateTime().equals(clock.now()); ) {
			queue.poll();

			T updateable = handle.get();

			updateable.update();

			if (updateable.getNextUpdateTime().isPresent()) {
				// placing after earlier actions with the same nextUpdateTime
				handle.refreshOrder(newOrder());
				queue.add(handle);
			}
		}
	}

	public Optional<Time> getNextUpdateTime() {
		if (queue.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(queue.peek().getNextUpdateTime());
	}

	public void remove(Handle<T> handle) {
		boolean removed = queue.remove(handle);

		if (!removed) {
			throw new IllegalArgumentException("Remove failed");
		}

		handle.get().onRemovedFromQueue();
	}

	public void removeIf(Predicate<T> predicate) {
		queue.stream()
				.filter(x -> predicate.test(x.get()))
				.sorted(Handle.orderComparator())
				.forEach(this::remove);
	}

	public void clear() {
		while (!queue.isEmpty()) {
			Handle<T> handle = queue.poll();
			handle.get().onRemovedFromQueue();
		}
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public void setClock(Clock clock) {
		this.clock = clock;
	}

	private long newOrder() {
		return orderGenerator++;
	}

	public Collection<Handle<T>> getElements() {
		return Collections.unmodifiableCollection(queue);
	}
}
