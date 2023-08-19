package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.talents.TalentId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.time.Time;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.ResourceType.HEALTH;
import static wow.commons.model.spells.ResourceType.MANA;
import static wow.commons.model.spells.SpellId.SHADOWBURN;
import static wow.commons.model.spells.SpellId.*;
import static wow.commons.model.talents.TalentId.*;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class SimulationTest extends WowSimulatorSpringTest {
	@Test
	void noObjects() {
		simulation = new Simulation(simulationContext);
		simulation.updateUntil(Time.at(30));

		assertEvents();
	}

	@Test
	void noActions() {
		simulation.updateUntil(Time.at(30));

		assertEvents();
	}

	@Test
	void delayedAction() {
		List<String> result = new ArrayList<>();

		simulation.delayedAction(Duration.seconds(5), () -> result.add(getClock().now() + ""));

		simulation.updateUntil(Time.at(4));

		assertThat(result).isEmpty();

		simulation.updateUntil(Time.at(5));

		assertThat(result).isEqualTo(List.of("5.000"));
	}

	@Test
	void singleSBCast() {
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, target),
				at(3)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void twoSBCasts() {
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, target),
				at(3)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
						.beginCast(player, SHADOW_BOLT, target),
				at(6)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void sbWithBane() {
		enableTalent(BANE, 5);

		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, target),
				at(2.5)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void sbWithCataclysm() {
		enableTalent(CATACLYSM, 5);

		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, target),
				at(3)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(399, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void shadowBurn() {
		enableTalent(TalentId.SHADOWBURN, 1);

		player.cast(SHADOWBURN);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN, target)
						.endCast(player, SHADOWBURN, target)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.decreasedResource(631, HEALTH, target, SHADOWBURN)
						.beginGcd(player, SHADOWBURN, target),
				at(1.5)
						.endGcd(player, SHADOWBURN, target)
		);
	}

	@Test
	void lifeTap() {
		setMana(player, 0);

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(582);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP, player)
						.endCast(player, LIFE_TAP, player)
						.decreasedResource(582, HEALTH, player, LIFE_TAP)
						.increasedResource(582, MANA, player, LIFE_TAP)
						.beginGcd(player, LIFE_TAP, player),
				at(1.5)
						.endGcd(player, LIFE_TAP, player)
		);
	}

	@Test
	void lifeTapWithTalent() {
		enableTalent(IMPROVED_LIFE_TAP, 2);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(698);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP, player)
						.endCast(player, LIFE_TAP, player)
						.decreasedResource(698, HEALTH, player, LIFE_TAP)
						.increasedResource(698, MANA, player, LIFE_TAP)
						.beginGcd(player, LIFE_TAP, player),
				at(1.5)
						.endGcd(player, LIFE_TAP, player)
		);
	}

	@Test
	void lifeTapWithTalentAndSp() {
		enableTalent(IMPROVED_LIFE_TAP, 2);
		enableBuff(BuffId.FEL_ARMOR, 2);
		setMana(player, 0);

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(794);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP, player)
						.endCast(player, LIFE_TAP, player)
						.decreasedResource(794, HEALTH, player, LIFE_TAP)
						.increasedResource(794, MANA, player, LIFE_TAP)
						.beginGcd(player, LIFE_TAP, player),
				at(1.5)
						.endGcd(player, LIFE_TAP, player)
		);
	}

	@Test
	void lifeTapWithT3Bonus() {
		enableTalent(IMPROVED_LIFE_TAP, 2);
		setMana(player, 0);

		equip("Plagueheart Belt");
		equip("Plagueheart Bindings");
		equip("Plagueheart Circlet");
		equip("Plagueheart Gloves");
		equip("Plagueheart Leggings");
		equip("Plagueheart Robe");
		equip("Plagueheart Sandals");
		equip("Plagueheart Shoulderpads");

		player.cast(LIFE_TAP);

		simulation.updateUntil(Time.at(30));

		assertThat(player.getCurrentMana()).isEqualTo(959);

		assertEvents(
				at(0)
						.beginCast(player, LIFE_TAP, player)
						.endCast(player, LIFE_TAP, player)
						.decreasedResource(863, HEALTH, player, LIFE_TAP)
						.increasedResource(959, MANA, player, LIFE_TAP)
						.beginGcd(player, LIFE_TAP, player),
				at(1.5)
						.endGcd(player, LIFE_TAP, player)
		);
	}

	@BeforeEach
	void setUp() {
		setupTestObjects();

		handler = new EventCollectingHandler();

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);
	}
}