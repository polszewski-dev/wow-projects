package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerProfile;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
abstract class ControllerTest extends WowMinMaxSpringTest {
	PlayerProfile profile;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
	}
}
