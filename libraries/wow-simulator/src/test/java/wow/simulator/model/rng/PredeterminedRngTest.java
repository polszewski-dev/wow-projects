package wow.simulator.model.rng;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
class PredeterminedRngTest {
	@Test
	void hitRoll() {
		PredeterminedRng rng = new PredeterminedRng();

		assertThat(rng.hitRoll(80, SHADOW_BOLT)).isTrue();
		assertThat(rng.hitRoll(80, SHADOW_BOLT)).isFalse();
		assertThat(rng.hitRoll(80, SHADOW_BOLT)).isTrue();
		assertThat(rng.hitRoll(80, SHADOW_BOLT)).isTrue();
		assertThat(rng.hitRoll(80, SHADOW_BOLT)).isTrue();
		assertThat(rng.hitRoll(80, SHADOW_BOLT)).isTrue();
	}

	@Test
	void critRoll() {
		PredeterminedRng rng = new PredeterminedRng();

		assertThat(rng.critRoll(40, SHADOW_BOLT)).isFalse();
		assertThat(rng.critRoll(40, SHADOW_BOLT)).isFalse();
		assertThat(rng.critRoll(40, SHADOW_BOLT)).isTrue();
		assertThat(rng.critRoll(40, SHADOW_BOLT)).isFalse();
		assertThat(rng.critRoll(40, SHADOW_BOLT)).isTrue();
		assertThat(rng.critRoll(40, SHADOW_BOLT)).isFalse();
	}
}