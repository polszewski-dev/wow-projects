package wow.simulator.simulation.spell.tbc.ability.paladin.retrition;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.SANCTITY_AURA;
import static wow.test.commons.AbilityNames.SMITE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class SanctityAuraTest extends TbcPaladinSpellSimulationTest {
	/*
	Increases Holy damage done by party members within 30 yards by 10%.
	Players may only have one Aura on them per Paladin at any one time.
	 */

	@Test
	void success() {
		player.cast(SANCTITY_AURA);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SANCTITY_AURA)
						.beginGcd(player)
						.endCast(player, SANCTITY_AURA)
						.effectApplied(SANCTITY_AURA, player, Duration.INFINITE),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void damage_is_increased() {
		player.cast(SANCTITY_AURA);
		player2.cast(SMITE);

		updateUntil(30);

		assertDamageDone(SMITE_INFO, player2.getTarget(), 0, 10);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		partyMemberClassId = CharacterClassId.PRIEST;
		partyMemberRaceId = RaceId.UNDEAD;
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.SANCTITY_AURA, 1);

		player.getParty().add(player2);
	}
}
