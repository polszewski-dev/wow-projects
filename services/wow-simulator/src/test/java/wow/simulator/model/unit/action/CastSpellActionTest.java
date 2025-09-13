package wow.simulator.model.unit.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.spell.AbilityId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.ActionStatus;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.PrimaryTarget;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.TalentNames.AMPLIFY_CURSE;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
class CastSpellActionTest extends WowSimulatorSpringTest {
	@Test
	void cast() {
		CastSpellAction action = getCastSpellAction(AbilityId.CORRUPTION);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(12));

		clock.advanceTo(Time.at(12));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void castShorterThanGcd() {
		enableTalent(IMPROVED_CORRUPTION, 4);

		CastSpellAction action = getCastSpellAction(AbilityId.CORRUPTION);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10.4));

		clock.advanceTo(Time.at(10.4));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void instant() {
		enableTalent(IMPROVED_CORRUPTION, 5);

		CastSpellAction action = getCastSpellAction(AbilityId.CORRUPTION);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void instantIgnoringGcd() {
		enableTalent(AMPLIFY_CURSE, 1);

		CastSpellAction action = getCastSpellOnSelfAction(AbilityId.AMPLIFY_CURSE);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void channel() {
		CastSpellAction action = getCastSpellAction(AbilityId.DRAIN_LIFE);

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void cantPayManaCost() {
		CastSpellAction action = getCastSpellAction(AbilityId.CORRUPTION);

		setMana(player, 0);

		assertThat(player.getCurrentMana()).isZero();

		clock.advanceTo(Time.at(10));

		action.start();
		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	CastSpellAction getCastSpellAction(AbilityId abilityId) {
		return getSpellAction(abilityId, PrimaryTarget.ofEnemy(target));
	}

	CastSpellAction getCastSpellOnSelfAction(AbilityId abilityId) {
		return getSpellAction(abilityId, PrimaryTarget.ofSelf(player));
	}

	private CastSpellAction getSpellAction(AbilityId abilityId, PrimaryTarget primaryTarget) {
		var ability = player.getAbility(abilityId).orElseThrow();
		return new CastSpellAction(player, ability, primaryTarget);
	}

	@BeforeEach
	void setup() {
		setupTestObjects();

		simulation.add(player);
		simulation.add(target);
	}
}