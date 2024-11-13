package wow.simulator.model.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Cost;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.effect.impl.TickingEffectInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.DRAIN_LIFE;
import static wow.commons.model.spell.AbilityId.LIFE_TAP;


/**
 * User: POlszewski
 * Date: 2023-08-15
 */
class ConversionsTest extends WowSimulatorSpringTest {
	@Test
	void performPaidCostConversion() {
		setHealth(player, 1000);
		setMana(player, 1000);

		var conversions = getSpellCastConversions(LIFE_TAP);
		Cost cost = player.getSpellCostSnapshot(LIFE_TAP).getCostToPay();

		conversions.performPaidCostConversion(cost);

		assertThat(player.getCurrentHealth()).isEqualTo(1000);//unchanged, because the cost is supposed to be paid elsewhere
		assertThat(player.getCurrentMana()).isEqualTo(1582);
	}

	@Test
	void performDamageDoneConversion() {
		setHealth(player, 1000);
		setMana(player, 1000);

		setHealth(target, 1000);
		setMana(target, 1000);

		var conversions = getEffectUpdateConversions(DRAIN_LIFE);

		conversions.performDamageDoneConversion(500);

		assertThat(player.getCurrentHealth()).isEqualTo(1500);
		assertThat(player.getCurrentMana()).isEqualTo(1000);

		assertThat(target.getCurrentHealth()).isEqualTo(1000);//unchanged, because the damage is supposed to be deduced elsewhere
		assertThat(target.getCurrentMana()).isEqualTo(1000);
	}

	SpellCastConversions getSpellCastConversions(AbilityId abilityId) {
		var ability = player.getAbility(abilityId).orElseThrow();
		return new SpellCastConversions(player, ability);
	}

	EffectUpdateConversions getEffectUpdateConversions(AbilityId abilityId) {
		var ability = player.getAbility(abilityId).orElseThrow();
		var effect = new TickingEffectInstance(
				player,
				target,
				ability.getEffectApplication().effect(),
				ability,
				Duration.seconds(5),
				Duration.seconds(1),
				1,
				1
		);
		return new EffectUpdateConversions(player, effect);
	}

	@BeforeEach
	void setUp() {
		setupTestObjects();
	}
}