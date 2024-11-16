package wow.simulator.model.update;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
public class StagedUpdateQueue<T extends StageUpdateable> extends AbstractUpdateQueue<T> {
	@Override
	public void updateAllPresentActions() {
		var toUpdate = pollPresentActions();

		for (var updateStage : UpdateStage.values()) {
			for (var handle : toUpdate) {
				handle.get().update(updateStage);
			}
		}

		for (var handle : toUpdate) {
			enqueueIfHasMoreUpdates(handle);
		}
	}

	private List<Handle<T>> pollPresentActions() {
		var toUpdate = new ArrayList<Handle<T>>();

		for (Handle<T> handle; (handle = queue.peek()) != null && clock.timeInThePresent(handle.getNextUpdateTime()); ) {
			queue.poll();
			toUpdate.add(handle);
		}
		return toUpdate;
	}
}
