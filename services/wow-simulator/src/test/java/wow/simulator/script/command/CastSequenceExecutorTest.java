package wow.simulator.script.command;

import org.junit.jupiter.api.Test;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class CastSequenceExecutorTest extends CommandExecutorTest {
	@Test
	void allConditionsAreMet_both_commands_activated_at_the_same_time() {
		var executor = getCastSequenceExecutor(
				useItem(TRINKET_1),
				castSpell(CURSE_OF_AGONY)
		);

		assertResultAfter(0, executor::allConditionsAreMet, true);

		executor.execute();

		assertResultAfter(1, executor::allConditionsAreMet, false);
		assertResultAfter(24, executor::allConditionsAreMet, false);
		assertResultAfter(89, executor::allConditionsAreMet, false);
		assertResultAfter(90, executor::allConditionsAreMet, true);

		updateUntil(120);
	}

	@Test
	void allConditionsAreMet_trinket_activated_already() {
		var executor = getCastSequenceExecutor(
				useItem(TRINKET_1),
				castSpell(CURSE_OF_AGONY)
		);

		var trinketExecutor = getUseItemExecutor(TRINKET_1);

		assertResultAfter(0, executor::allConditionsAreMet, true);

		trinketExecutor.execute();

		assertResultAfter(1, executor::allConditionsAreMet, false);
		assertResultAfter(89, executor::allConditionsAreMet, false);
		assertResultAfter(90, executor::allConditionsAreMet, true);

		updateUntil(120);
	}

	@Test
	void allConditionsAreMet_ability_activated_already() {
		var executor = getCastSequenceExecutor(
				useItem(TRINKET_1),
				castSpell(CURSE_OF_AGONY)
		);

		var abilityExecutor = getCastSpellExecutor(CURSE_OF_AGONY);

		assertResultAfter(0, executor::allConditionsAreMet, true);

		abilityExecutor.execute();

		assertResultAfter(1, executor::allConditionsAreMet, false);
		assertResultAfter(23, executor::allConditionsAreMet, false);
		assertResultAfter(24, executor::allConditionsAreMet, true);

		updateUntil(120);
	}

	@Override
	protected void afterSetUp() {
		equip(SCRYERS_BLOODGEM, TRINKET_1);
	}

	static final String SCRYERS_BLOODGEM = "Scryer's Bloodgem";
}