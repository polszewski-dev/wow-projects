package wow.simulator.model.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.simulator.WowSimulatorSpringTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.SpellId.DRAIN_LIFE;
import static wow.commons.model.spells.SpellId.LIFE_TAP;


/**
 * User: POlszewski
 * Date: 2023-08-15
 */
class SpellConversionsTest extends WowSimulatorSpringTest {
	@Test
	void performPaidCostConversion() {
		setHealth(player, 1000);
		setMana(player, 1000);

		SpellConversions conversions = getConversions(LIFE_TAP, player);

		conversions.performPaidCostConversion();

		assertThat(player.getCurrentHealth()).isEqualTo(1000);//unchanged, because the cost is supposed to be paid elsewhere
		assertThat(player.getCurrentMana()).isEqualTo(1582);
	}

	@Test
	void performDamageDoneConversion() {
		setHealth(player, 1000);
		setMana(player, 1000);

		setHealth(target, 1000);
		setMana(target, 1000);

		SpellConversions conversions = getConversions(DRAIN_LIFE, target);

		conversions.performDamageDoneConversion(500);

		assertThat(player.getCurrentHealth()).isEqualTo(1500);
		assertThat(player.getCurrentMana()).isEqualTo(1000);

		assertThat(target.getCurrentHealth()).isEqualTo(1000);//unchanged, because the damage is supposed to be deduced elsewhere
		assertThat(target.getCurrentMana()).isEqualTo(1000);
	}

	SpellConversions getConversions(SpellId spellId, Unit spellTarget) {
		Spell spell = player.getSpell(spellId).orElseThrow();
		SpellCastContext context = player.getSpellCastContext(spell, spellTarget);
		return context.getConversions();
	}

	@BeforeEach
	void setUp() {
		setupTestObjects();
	}
}