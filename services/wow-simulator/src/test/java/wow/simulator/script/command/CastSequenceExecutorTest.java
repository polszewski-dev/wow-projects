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
	void both_commands_activated_at_the_same_time() {
		var executor = getCastSequenceExecutor(
				useItem(TRINKET_1),
				castSpell(CURSE_OF_AGONY)
		);

		snapshotAt(executor, 0, 1, 24, 89, 90);

		executor.execute();

		updateUntil(120);

		assertResultAt(0, true);
		assertResultAt(1, false);
		assertResultAt(24, false);
		assertResultAt(89, false);
		assertResultAt(90, true);
	}

	@Test
	void trinket_activated_already() {
		var executor = getCastSequenceExecutor(
				useItem(TRINKET_1),
				castSpell(CURSE_OF_AGONY)
		);

		var trinketExecutor = getUseItemExecutor(TRINKET_1);

		snapshotAt(executor, 0, 1, 89, 90);

		trinketExecutor.execute();

		updateUntil(120);

		assertResultAt(0, true);
		assertResultAt(1, false);
		assertResultAt(89, false);
		assertResultAt(90, true);
	}

	@Test
	void ability_activated_already() {
		var executor = getCastSequenceExecutor(
				useItem(TRINKET_1),
				castSpell(CURSE_OF_AGONY)
		);

		var abilityExecutor = getCastSpellExecutor(CURSE_OF_AGONY);

		snapshotAt(executor, 0, 1, 23, 24);

		abilityExecutor.execute();

		updateUntil(120);

		assertResultAt(0, true);
		assertResultAt(1, false);
		assertResultAt(23, false);
		assertResultAt(24, true);
	}

	@Override
	protected void afterSetUp() {
		equip(SCRYERS_BLOODGEM, TRINKET_1);
	}

	static final String SCRYERS_BLOODGEM = "Scryer's Bloodgem";
}