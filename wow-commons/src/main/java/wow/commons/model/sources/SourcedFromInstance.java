package wow.commons.model.sources;

import wow.commons.model.pve.Instance;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
abstract class SourcedFromInstance extends Source {
	protected final Instance instance;

	SourcedFromInstance(Instance instance, Phase phase) {
		super(phase != null ? phase : instance.getPhase());
		this.instance = instance;
	}

	@Override
	public final Instance getInstance() {
		return instance;
	}
}
