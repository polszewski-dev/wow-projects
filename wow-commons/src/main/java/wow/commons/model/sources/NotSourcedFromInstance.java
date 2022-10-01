package wow.commons.model.sources;

import wow.commons.model.pve.Instance;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
abstract class NotSourcedFromInstance extends Source {
	NotSourcedFromInstance(Integer phase) {
		super(phase);
	}

	@Override
	public final Instance getInstance() {
		return null;
	}
}
