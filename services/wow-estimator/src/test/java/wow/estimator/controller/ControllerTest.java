package wow.estimator.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.estimator.WowEstimatorSpringTest;
import wow.estimator.converter.PlayerConverter;
import wow.estimator.model.Player;

/**
 * User: POlszewski
 * Date: 2025-03-19
 */
abstract class ControllerTest extends WowEstimatorSpringTest {
	@Autowired
	PlayerConverter playerConverter;

	Player character;

	@BeforeEach
	void setup() {
		character = getCharacter();

		equipGearSet(character);
	}
}
