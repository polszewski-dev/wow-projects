package wow.minmax.repository;

import wow.commons.model.character.Build;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-01-15
 */
public interface BuildRepository {
	Optional<Build> getBuild(String buildId, int level);
}
