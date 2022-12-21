package wow.minmax.service;

import org.junit.jupiter.api.BeforeEach;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.PlayerProfile;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowMinMaxSpringTest {
	PlayerProfile profile;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
	}
}
