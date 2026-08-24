package wow.simulator.simulation.spell.tbc.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.commons.model.character.PetType;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-08-22
 */
class ShadowfiendTest extends TbcPriestSpellSimulationTest {
	@Test
	void success() {
		player.cast(SHADOWFIEND);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOWFIEND)
						.beginGcd(player)
						.endCast(player, SHADOWFIEND)
						.decreasedResource(269, MANA, player, SHADOWFIEND)
						.cooldownStarted(player, SHADOWFIEND, 300)
						.petSummoned(player, PetType.SHADOWFIEND),
				at(1.5)
						.endGcd(player),
				atMillis(15_001)
						.petUnsummoned(player, PetType.SHADOWFIEND)
		);
	}

	@Test
	void shadowfiend_attacks_target_if_player_is_in_combat() {
		setMana(player, 1000);
		player.cast(MIND_BLAST);
		player.cast(SHADOWFIEND);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MIND_BLAST, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, MIND_BLAST)
						.decreasedResource(450, MANA, player, MIND_BLAST)
						.cooldownStarted(player, MIND_BLAST, 8)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.endGcd(player)
						.beginCast(player, SHADOWFIEND)
						.beginGcd(player)
						.endCast(player, SHADOWFIEND)
						.decreasedResource(269, MANA, player, SHADOWFIEND)
						.cooldownStarted(player, SHADOWFIEND, 300)
						.petSummoned(player, PetType.SHADOWFIEND)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(3)
						.endGcd(player)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(4.5)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(6)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(7.5)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(9)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(9.5)
						.cooldownExpired(player, MIND_BLAST),
				at(10.5)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(12)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(13.5)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(15)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet)
						.beginCast(pet, ATTACK, 1.5)
						.beginGcd(pet),
				at(16.5)
						.endCast(pet, ATTACK)
						.decreasedResource(110, HEALTH, target, ATTACK)
						.increasedResource(275, MANA, player, ATTACK)
						.endGcd(pet),
				atMillis(16_501)
						.petUnsummoned(player, PetType.SHADOWFIEND)
		);
	}

	@Override
	public void petUnsummoned(Unit master, Pet pet) {
		// do nothing
	}
}
