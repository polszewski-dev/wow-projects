package wow.commons.model.sources;

import wow.commons.model.pve.Zone;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
abstract class SourcedFromInstance extends Source {
	protected final Zone instance;

	SourcedFromInstance(Zone instance) {
		this.instance = instance;
	}

	@Override
	public final Zone getInstance() {
		return instance;
	}
}
