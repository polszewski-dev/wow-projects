package wow.simulator.simulation.spell.misc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.spell.AbilityId.*;
import static wow.simulator.WowSimulatorSpringTest.EventCollectingHandler.Event;

/**
 * User: POlszewski
 * Date: 2024-11-23
 */
class SpellTargetTest extends SpellSimulationTest {
	enum DefaultTarget {
		NONE,
		ENEMY_1,
		FRIEND_1
	}

	enum SpecifiedTarget {
		NONE,
		ENEMY_2,
		FRIEND_2
	}


	enum ExpectedTarget {
		SELF,
		ENEMY_1,
		ENEMY_2,
		FRIEND_1,
		FRIEND_2,
		INVALID
	}

	@ParameterizedTest
	@CsvSource({
			"NONE,     NONE,     SELF",
			"NONE,     ENEMY_2,  INVALID",
			"NONE,     FRIEND_2, INVALID",
			"ENEMY_1,  NONE,     SELF",
			"ENEMY_1,  ENEMY_2,  INVALID",
			"ENEMY_1,  FRIEND_2, INVALID",
			"FRIEND_1, NONE,     SELF",
			"FRIEND_1, ENEMY_2,  INVALID",
			"FRIEND_1, FRIEND_2, INVALID",
	})
	void selfBuff(DefaultTarget defaultTarget, SpecifiedTarget specifiedTarget, ExpectedTarget expectedTarget) {
		assertAppliedEffectOnTarget(INNER_FIRE, defaultTarget, specifiedTarget, expectedTarget);
	}

	@ParameterizedTest
	@CsvSource({
			"NONE,     NONE,     SELF",
			"NONE,     ENEMY_2,  INVALID",
			"NONE,     FRIEND_2, FRIEND_2",
			"ENEMY_1,  NONE,     SELF",
			"ENEMY_1,  ENEMY_2,  INVALID",
			"ENEMY_1,  FRIEND_2, FRIEND_2",
			"FRIEND_1, NONE,     FRIEND_1",
			"FRIEND_1, ENEMY_2,  INVALID",
			"FRIEND_1, FRIEND_2, FRIEND_2",
	})
	void buffOnTarget(DefaultTarget defaultTarget, SpecifiedTarget specifiedTarget, ExpectedTarget expectedTarget) {
		assertAppliedEffectOnTarget(POWER_WORD_FORTITUDE, defaultTarget, specifiedTarget, expectedTarget);
	}

	@ParameterizedTest
	@CsvSource({
			"NONE,     NONE,     INVALID",
			"NONE,     ENEMY_2,  ENEMY_2",
			"NONE,     FRIEND_2, INVALID",
			"ENEMY_1,  NONE,     ENEMY_1",
			"ENEMY_1,  ENEMY_2,  ENEMY_2",
			"ENEMY_1,  FRIEND_2, INVALID",
			"FRIEND_1, NONE,     INVALID",
			"FRIEND_1, ENEMY_2,  ENEMY_2",
			"FRIEND_1, FRIEND_2, INVALID",
	})
	void debuffOnTarget(DefaultTarget defaultTarget, SpecifiedTarget specifiedTarget, ExpectedTarget expectedTarget) {
		assertAppliedEffectOnTarget(SHADOW_WORD_PAIN, defaultTarget, specifiedTarget, expectedTarget);
	}

	private void assertAppliedEffectOnTarget(AbilityId abilityId, DefaultTarget defaultTarget, SpecifiedTarget specifiedTarget, ExpectedTarget expectedTarget) {
		setDefaultTarget(defaultTarget);

		cast(abilityId, specifiedTarget);

		simulation.updateUntil(Time.at(30));

		if (expectedTarget != ExpectedTarget.INVALID) {
			assertThat(expectedTarget).isNotEqualTo(ExpectedTarget.INVALID);

			assertEvents(
					Event::isEffectApplied,
					at(0).effectApplied(abilityId, getExpectedTarget(expectedTarget))
			);
		} else {
			assertThat(expectedTarget).isEqualTo(ExpectedTarget.INVALID);

			assertEvents(
					at(0).canNotBeCasted(player, abilityId)
			);
		}
	}

	private void setDefaultTarget(DefaultTarget defaultTarget) {
		switch (defaultTarget) {
			case NONE -> player.setTarget(null);
			case ENEMY_1 -> player.setTarget(enemy);
			case FRIEND_1 -> player.setTarget(friend);
		}
	}

	private void cast(AbilityId abilityId, SpecifiedTarget specifiedTarget) {
		switch (specifiedTarget) {
			case NONE -> player.cast(abilityId);
			case ENEMY_2 -> player.cast(abilityId, otherEnemy);
			case FRIEND_2 -> player.cast(abilityId, otherFriend);
		}
	}

	private Unit getExpectedTarget(ExpectedTarget expectedTarget) {
		return switch (expectedTarget) {
			case SELF -> player;
			case FRIEND_1 -> friend;
			case FRIEND_2 -> otherFriend;
			case ENEMY_1 -> enemy;
			case ENEMY_2 -> otherEnemy;
			default -> throw new IllegalArgumentException();
		};
	}

	Player friend;
	Player otherFriend;
	NonPlayer enemy;
	NonPlayer otherEnemy;

	@BeforeEach
	public void setUp() {
		characterClassId = PRIEST;

		super.setUp();

		friend = getNakedPlayer(WARLOCK, "Friend");
		otherFriend = getNakedPlayer(WARLOCK, "OtherFriend");
		enemy = getEnemy("Enemy");
		otherEnemy = getEnemy("OtherEnemy");

		simulation.add(friend);
		simulation.add(otherFriend);
		simulation.add(enemy);
		simulation.add(otherEnemy);
	}
}