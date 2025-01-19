package wow.simulator.model.update;

import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
public class UpdateQueue<T extends Updateable> extends AbstractUpdateQueue<T> {
	@Getter
	private T currentlyUpdatedElement;

	@Override
	public void updateAllPresentActions() {
		for (Handle<T> handle; (handle = queue.peek()) != null && clock.timeInThePresent(handle.getNextUpdateTime()); ) {
			queue.poll();

			T updateable = handle.get();

			this.currentlyUpdatedElement = updateable;

			updateable.update();

			enqueueIfHasMoreUpdates(handle);

			this.currentlyUpdatedElement = null;
		}
	}
}
