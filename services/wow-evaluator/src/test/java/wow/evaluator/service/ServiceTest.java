package wow.evaluator.service;

import org.junit.jupiter.api.BeforeEach;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.evaluator.WowEvaluatorSpringTest;
import wow.evaluator.model.Player;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowEvaluatorSpringTest {
	Player character;

	@BeforeEach
	void setup() {
		character = getCharacter();
	}

	Ability getAbility(AbilityId abilityId) {
		return character.getAbility(abilityId).orElseThrow();
	}
}
