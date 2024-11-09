package wow.minmax;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;
import wow.minmax.repository.PlayerCharacterRepository;
import wow.minmax.repository.PlayerProfileRepository;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character",
		"wow.minmax"
})
public class WowMinMaxSpringTestConfig {
	@MockBean
	PlayerProfileRepository playerProfileRepository;

	@MockBean
	PlayerCharacterRepository playerCharacterRepository;

	@MockBean
	@Qualifier("simulator")
	WebClient simulatorWebClient;
}
