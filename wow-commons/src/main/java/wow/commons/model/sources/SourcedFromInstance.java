package wow.commons.model.sources;

import wow.commons.model.pve.Instance;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
abstract class SourcedFromInstance extends Source {
	protected final Instance instance;

	SourcedFromInstance(Instance instance, Integer phase) {
		super(phase);
		if (instance == null) {
			throw new NullPointerException();
		}
		this.instance = instance;
	}

	@Override
	public final Instance getInstance() {
		return instance;
	}

	@Override
	protected int getDefaultPhase() {
		return instance.getPhase();
	}
}
