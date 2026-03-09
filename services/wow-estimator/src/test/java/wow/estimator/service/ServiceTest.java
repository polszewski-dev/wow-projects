package wow.estimator.service;

import org.junit.jupiter.api.BeforeEach;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.estimator.WowEstimatorSpringTest;
import wow.estimator.model.Player;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowEstimatorSpringTest {
	Player player;

	@BeforeEach
	void setup() {
		player = getPlayer();
	}

	Ability getAbility(AbilityId abilityId) {
		return player.getAbility(abilityId).orElseThrow();
	}
}
