package wow.commons.model.sources;

import wow.commons.model.pve.Zone;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
abstract class NotSourcedFromInstance extends Source {
	@Override
	public final Zone getInstance() {
		return null;
	}
}
