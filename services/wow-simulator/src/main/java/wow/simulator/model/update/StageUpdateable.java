package wow.simulator.model.update;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
public interface StageUpdateable extends AbstractUpdateable {
	void update(UpdateStage stage);
}
