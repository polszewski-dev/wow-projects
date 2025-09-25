package wow.simulator.model.rng;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.component.Event;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.WowSimulatorSpringTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
class PredeterminedRngTest extends WowSimulatorSpringTest {
	@Autowired
	SpellRepository spellRepository;

	@Test
	void hitRoll() {
		var rng = new PredeterminedRng();

		assertThat(rng.hitRoll(80, ability)).isTrue();
		assertThat(rng.hitRoll(80, ability)).isFalse();
		assertThat(rng.hitRoll(80, ability)).isTrue();
		assertThat(rng.hitRoll(80, ability)).isTrue();
		assertThat(rng.hitRoll(80, ability)).isTrue();
		assertThat(rng.hitRoll(80, ability)).isTrue();
	}

	@Test
	void critRoll() {
		var rng = new PredeterminedRng();

		assertThat(rng.critRoll(40, ability)).isFalse();
		assertThat(rng.critRoll(40, ability)).isFalse();
		assertThat(rng.critRoll(40, ability)).isTrue();
		assertThat(rng.critRoll(40, ability)).isFalse();
		assertThat(rng.critRoll(40, ability)).isTrue();
		assertThat(rng.critRoll(40, ability)).isFalse();
	}

	@Test
	void eventRoll() {
		var rng = new PredeterminedRng();

		assertThat(rng.eventRoll(50, event)).isFalse();
		assertThat(rng.eventRoll(50, event)).isTrue();
		assertThat(rng.eventRoll(50, event)).isFalse();
		assertThat(rng.eventRoll(50, event)).isTrue();
		assertThat(rng.eventRoll(50, event)).isFalse();
		assertThat(rng.eventRoll(50, event)).isTrue();
	}

	Ability ability;
	Event event;

	@BeforeEach
	void setUp() {
		ability = spellRepository.getAbility(SHADOW_BOLT, 1, PhaseId.TBC_P5).orElseThrow();
		event = spellRepository.getEffect(EffectId.of(17800), PhaseId.TBC_P5).orElseThrow().getEvents().getFirst();
	}
}