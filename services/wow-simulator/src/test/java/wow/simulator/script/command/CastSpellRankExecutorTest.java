package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.simulator.util.TestEvent;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class CastSpellRankExecutorTest extends CommandExecutorTest {
	@Test
	void correct_rank_is_cast() {
		simulate("/script/command/shadow-bolt-rank-4.txt", 3);

		assertEvents(
				TestEvent::isDamage,
				at(3).decreasedResource(98, HEALTH, target, SHADOW_BOLT)
		);
	}
}