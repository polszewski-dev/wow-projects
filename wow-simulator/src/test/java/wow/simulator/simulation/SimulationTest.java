package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.talents.TalentId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.time.Time;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.SpellId.LIFE_TAP;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class SimulationTest extends WowSimulatorSpringTest {
	@Test
	void noObjects() {
		simulation = new Simulation(simulationContext);
		simulation.updateUntil(Time.at(30));
		assertThat(handler.getEvents()).isEqualTo(List.of(
				"30.000> simulationEnded"
		));
	}

	@Test
	void noActions() {
		simulation.updateUntil(Time.at(30));
		assertThat(handler.getEvents()).isEqualTo(List.of(
				"30.000> simulationEnded"
		));
	}

	@Test
	void singleSBCast() {
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertThat(handler.getEvents()).isEqualTo(List.of(
				"0.000> beginCast: caster = Player, spell = Shadow Bolt (Rank 11), target = Target",
				"3.000> endCast: caster = Player, spell = Shadow Bolt (Rank 11), target = Target",
				"3.000> decreasedResource: target = Player, spell = Shadow Bolt (Rank 11), amount = 420, type = MANA",
				"3.000> decreasedResource: target = Target, spell = Shadow Bolt (Rank 11), amount = 534, type = HEALTH",
				"30.000> simulationEnded"
		));
	}

	@Test
	void twoSBCasts() {
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertThat(handler.getEvents()).isEqualTo(List.of(
				"0.000> beginCast: caster = Player, spell = Shadow Bolt (Rank 11), target = Target",
				"3.000> endCast: caster = Player, spell = Shadow Bolt (Rank 11), target = Target",
				"3.000> decreasedResource: target = Player, spell = Shadow Bolt (Rank 11), amount = 420, type = MANA",
				"3.000> decreasedResource: target = Target, spell = Shadow Bolt (Rank 11), amount = 534, type = HEALTH",
				"3.000> beginCast: caster = Player, spell = Shadow Bolt (Rank 11), target = Target",
				"6.000> endCast: caster = Player, spell = Shadow Bolt (Rank 11), target = Target",
				"6.000> decreasedResource: target = Player, spell = Shadow Bolt (Rank 11), amount = 420, type = MANA",
				"6.000> decreasedResource: target = Target, spell = Shadow Bolt (Rank 11), amount = 534, type = HEALTH",
				"30.000> simulationEnded"
		));
	}

	@Test
	void lifeTapWithoutTalent() {
		setMana(player, 0);
		handler.getEvents().clear();

		assertThat(player.getCurrentMana()).isZero();

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(582);

		assertThat(handler.getEvents()).isEqualTo(List.of(
				"0.000> beginCast: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"0.000> endCast: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"0.000> decreasedResource: target = Player, spell = Life Tap (Rank 7), amount = 582, type = HEALTH",
				"0.000> increasedResource: target = Player, spell = Life Tap (Rank 7), amount = 582, type = MANA",
				"0.000> beginGcd: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"1.500> endGcd: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"30.000> simulationEnded"
		));
	}

	@Test
	void lifeTapWithTalent() {
		player.getCharacter().getTalents().enableTalent(TalentId.IMPROVED_LIFE_TAP, 2);
		getCharacterService().updateAfterRestrictionChange(player.getCharacter());

		setMana(player, 0);
		handler.getEvents().clear();

		assertThat(player.getCurrentMana()).isZero();

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(698);

		assertThat(handler.getEvents()).isEqualTo(List.of(
				"0.000> beginCast: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"0.000> endCast: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"0.000> decreasedResource: target = Player, spell = Life Tap (Rank 7), amount = 698, type = HEALTH",
				"0.000> increasedResource: target = Player, spell = Life Tap (Rank 7), amount = 698, type = MANA",
				"0.000> beginGcd: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"1.500> endGcd: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"30.000> simulationEnded"
		));
	}

	@Test
	void lifeTapWithTalentAndSp() {
		player.getCharacter().getTalents().enableTalent(TalentId.IMPROVED_LIFE_TAP, 2);
		getCharacterService().updateAfterRestrictionChange(player.getCharacter());

		player.getCharacter().getBuffs().enable(BuffId.FEL_ARMOR, 2);

		setMana(player, 0);
		handler.getEvents().clear();

		assertThat(player.getCurrentMana()).isZero();

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(794);

		assertThat(handler.getEvents()).isEqualTo(List.of(
				"0.000> beginCast: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"0.000> endCast: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"0.000> decreasedResource: target = Player, spell = Life Tap (Rank 7), amount = 794, type = HEALTH",
				"0.000> increasedResource: target = Player, spell = Life Tap (Rank 7), amount = 794, type = MANA",
				"0.000> beginGcd: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"1.500> endGcd: caster = Player, spell = Life Tap (Rank 7), target = Player",
				"30.000> simulationEnded"
		));
	}

	Simulation simulation;
	EventCollectingHandler handler;

	@BeforeEach
	void setup() {
		setupTestObjects();
		simulation = new Simulation(simulationContext);

		handler = new EventCollectingHandler();

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);
	}
}