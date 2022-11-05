package wow.minmax.repository;

import wow.minmax.model.Build;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-01-15
 */
public interface BuildRepository {
	Optional<Build> getBuild(String buildId);
}
