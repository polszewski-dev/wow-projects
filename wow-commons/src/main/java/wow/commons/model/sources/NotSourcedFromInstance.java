package wow.commons.model.sources;

import wow.commons.model.pve.Instance;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
abstract class NotSourcedFromInstance extends Source {
	NotSourcedFromInstance(Phase phase) {
		super(phase);
	}

	@Override
	public final Instance getInstance() {
		return null;
	}
}
