package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.DRANEI;
import static wow.commons.model.character.RaceId.HUMAN;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SYMBOL_OF_HOPE;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
class SymbolOfHopeTest extends TbcPriestSpellSimulationTest {
	/*
	Greatly increases the morale of party members, giving them 33 mana every 5 sec.  Effect lasts 15 sec.
	 */

	@ParameterizedTest
	@ValueSource(strings = { "empty", "self", "partyMember", "otherPartyMember", "enemy" })
	void targetingEnemy(String targetToSelect) {
		switch (targetToSelect) {
			case "empty" -> player.setTarget(null);
			case "self" -> player.setTarget(player);
			case "partyMember" -> player.setTarget(player2);
			case "otherPartyMember" -> player.setTarget(player3);
			case "enemy" -> player.setTarget(target);
			default -> throw new IllegalArgumentException(targetToSelect);
		}

		player.cast(SYMBOL_OF_HOPE);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, SYMBOL_OF_HOPE)
						.beginGcd(player)
						.endCast(player, SYMBOL_OF_HOPE)
						.decreasedResource(15, MANA, player, SYMBOL_OF_HOPE)
						.cooldownStarted(player, SYMBOL_OF_HOPE, 300)
						.effectApplied(SYMBOL_OF_HOPE, player, 15)
						.effectApplied(SYMBOL_OF_HOPE, player2, 15),
				at(1.5)
						.endGcd(player),
				at(15)
						.effectExpired(SYMBOL_OF_HOPE, player)
						.effectExpired(SYMBOL_OF_HOPE, player2)
		);
	}

	@Override
	protected void beforeSetUp() {
		characterClassId = PRIEST;
		raceId = DRANEI;
		partyMemberRaceId = HUMAN;
	}

	@Override
	protected void afterSetUp() {
		player.getParty().add(player2);
		player3.getParty().add(player4);
	}
}
