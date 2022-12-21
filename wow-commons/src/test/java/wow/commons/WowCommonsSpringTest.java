package wow.commons;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.PveRepository;
import wow.commons.repository.SpellRepository;

import java.util.Comparator;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsSpringTestConfig.class)
public abstract class WowCommonsSpringTest {
	@Autowired
	protected ItemRepository itemRepository;

	@Autowired
	protected SpellRepository spellRepository;

	@Autowired
	protected PveRepository pveRepository;

	protected static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	protected static final Offset<Double> PRECISION = Offset.offset(0.01);
}
