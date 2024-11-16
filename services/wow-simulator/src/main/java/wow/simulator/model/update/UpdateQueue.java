package wow.simulator.model.update;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
public class UpdateQueue<T extends Updateable> extends AbstractUpdateQueue<T> {
	@Override
	public void updateAllPresentActions() {
		for (Handle<T> handle; (handle = queue.peek()) != null && clock.timeInThePresent(handle.getNextUpdateTime()); ) {
			queue.poll();

			T updateable = handle.get();

			updateable.update();

			enqueueIfHasMoreUpdates(handle);
		}
	}
}
