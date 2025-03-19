package wow.evaluator.client;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowEvaluatorClientSpringTestConfig.class)
public abstract class WowEvaluatorClientSpringTest {

}
