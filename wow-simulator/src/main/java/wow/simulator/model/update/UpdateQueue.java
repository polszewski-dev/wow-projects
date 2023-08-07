package wow.simulator.model.update;

import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.TimeAware;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
public class UpdateQueue implements TimeAware {
	private record QueueElement(
			Updateable updateable,
			int order
	) {
		Time getNextUpdateTime() {
			return updateable.getNextUpdateTime().orElseThrow();
		}
	}

	private static final Comparator<QueueElement> QUEUE_ELEMENT_COMPARATOR = Comparator
			.comparing(QueueElement::getNextUpdateTime)
			.thenComparingInt(QueueElement::order);

	private final PriorityQueue<QueueElement> queue = new PriorityQueue<>(QUEUE_ELEMENT_COMPARATOR);
	private int orderGenerator;
	private Clock clock;

	public void add(Updateable updateable) {
		if (updateable.getNextUpdateTime().isEmpty()) {
			throw new IllegalArgumentException();
		}
		var element = new QueueElement(updateable, orderGenerator++);
		queue.add(element);
	}

	public void updateAllPresentActions() {
		for (QueueElement queueElement; (queueElement = queue.peek()) != null && queueElement.getNextUpdateTime().equals(clock.now()); ) {
			queue.poll();

			Updateable updateable = queueElement.updateable();

			updateable.update();

			if (updateable.getNextUpdateTime().isPresent()) {
				// placing after earlier actions with the same nextUpdateTime
				add(updateable);
			}
		}
	}

	public Optional<Time> getNextUpdateTime() {
		if (queue.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(queue.peek().getNextUpdateTime());
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public void setClock(Clock clock) {
		this.clock = clock;
	}
}
