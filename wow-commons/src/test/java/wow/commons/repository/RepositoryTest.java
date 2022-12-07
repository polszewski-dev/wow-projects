package wow.commons.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.WowCommonsTestConfig;
import wow.commons.model.pve.Phase;

import static wow.commons.model.pve.Phase.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsTestConfig.class)
abstract class RepositoryTest {
	static final Phase PHASE = TBC_P5;
}
