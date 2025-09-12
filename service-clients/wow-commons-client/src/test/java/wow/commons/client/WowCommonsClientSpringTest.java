package wow.commons.client;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.Enchant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsClientSpringTestConfig.class)
public abstract class WowCommonsClientSpringTest {
	protected static void assertId(AbstractItem<?> item, int id) {
		assertThat(item.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Enchant enchant, int id) {
		assertThat(enchant.getId().value()).isEqualTo(id);
	}
}
