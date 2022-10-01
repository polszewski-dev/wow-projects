package wow.minmax.repository;

import wow.minmax.model.Build;

/**
 * User: POlszewski
 * Date: 2022-01-15
 */
public interface BuildRepository {
	Build getBuild(String buildId);
}
