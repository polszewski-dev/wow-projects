package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.character.FormType;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;
import wow.test.commons.TalentNames;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MOONKIN_FORM;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.EffectNames.MOONKIN_AURA;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class MoonkinFormTest extends TbcDruidSpellSimulationTest {
	/*
	Shapeshift into Moonkin Form. While in this form the armor contribution from items is increased by 400%,
	attack power is increased by 150% of your level
	and all party members within 30 yards have their spell critical chance increased by 5%.
	Melee attacks in this form have a chance on hit to regenerate mana based on attack power.
	The Moonkin can only cast Balance and Remove Curse spells while shapeshifted.
	The act of shapeshifting frees the caster of Polymorph and Movement Impairing effects.
	 */

	@Test
	void success() {
		player.cast(MOONKIN_FORM);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MOONKIN_FORM)
						.beginGcd(player)
						.endCast(player, MOONKIN_FORM)
						.decreasedResource(848, MANA, player, MOONKIN_FORM)
						.effectApplied(MOONKIN_FORM, player, Duration.INFINITE)
						.effectApplied(MOONKIN_AURA, player, Duration.INFINITE),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void crit_chance_increased() {
		player.idleFor(Duration.seconds(4));
		player.cast(MOONKIN_FORM);

		player2.cast(SHADOW_BOLT);
		player2.cast(SHADOW_BOLT);

		updateUntil(30);

		var baseCritChance = rng.getCritRollData().getRollChances().getFirst();

		assertLastCritChance(baseCritChance + 5);
	}

	@Test
	void correct_form_type() {
		player.cast(MOONKIN_FORM);

		updateUntil(30);

		assertThat(player.getForm()).isEqualTo(FormType.MOONKIN_FORM);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		setOtherPartyMemberConfig(WARLOCK, UNDEAD);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.MOONKIN_FORM);
	}
}
