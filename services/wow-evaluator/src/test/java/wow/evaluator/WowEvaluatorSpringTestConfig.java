package wow.evaluator;

import org.springframework.context.annotation.ComponentScan;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character",
		"wow.evaluator"
})
public class WowEvaluatorSpringTestConfig {
}
