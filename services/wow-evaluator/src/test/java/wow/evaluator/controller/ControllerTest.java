package wow.evaluator.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.evaluator.WowEvaluatorSpringTest;
import wow.evaluator.converter.PlayerConverter;
import wow.evaluator.model.Player;

/**
 * User: POlszewski
 * Date: 2025-03-19
 */
abstract class ControllerTest extends WowEvaluatorSpringTest {
	@Autowired
	PlayerConverter playerConverter;

	Player character;

	@BeforeEach
	void setup() {
		character = getCharacter();

		equipGearSet(character);
	}
}
