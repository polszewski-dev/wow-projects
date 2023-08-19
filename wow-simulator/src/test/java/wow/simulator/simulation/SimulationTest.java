package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
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
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(4.5)
						.endGcd(player),
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
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
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
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
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
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
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
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
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
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
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
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
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
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void coa() {
		player.cast(CURSE_OF_AGONY);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_AGONY, target)
						.endCast(player, CURSE_OF_AGONY, target)
						.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
						.effectApplied(CURSE_OF_AGONY, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(4)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(6)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(8)
						.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
				at(10)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(12)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(14)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(16)
						.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
				at(18)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(20)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(22)
						.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
				at(24)
						.decreasedResource(173, HEALTH, target, CURSE_OF_AGONY)
						.effectExpired(CURSE_OF_AGONY, target)
		);
	}

	@Test
	void corruption() {
		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION, target)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target),
				at(5)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(8)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(11)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(14)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(17)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(20)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@Test
	void corruptionWithIC() {
		enableTalent(IMPROVED_CORRUPTION, 5);

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, target)
						.endCast(player, CORRUPTION, target)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(6)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(150, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(150, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@Test
	void corruptionWithT5Bonus() {
		enableTalent(IMPROVED_CORRUPTION, 5);

		equip("Voidheart Crown");
		equip("Voidheart Gloves");
		equip("Voidheart Leggings");
		equip("Voidheart Mantle");

		player.cast(CORRUPTION);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, target)
						.endCast(player, CORRUPTION, target)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(6)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(9)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(12)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(15)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(18)
						.decreasedResource(175, HEALTH, target, CORRUPTION),
				at(21)
						.decreasedResource(175, HEALTH, target, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@Test
	void immolate() {
		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE, target)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target),
				at(5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(8)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@Test
	void immolateWithBane() {
		enableTalent(BANE, 5);

		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, target)
						.beginGcd(player),
				at(1.5)
						.endCast(player, IMMOLATE, target)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target)
						.endGcd(player),
				at(4.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(7.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(10.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(13.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(16.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@Test
	void immolateWithImprovedImmolate() {
		enableTalent(IMPROVED_IMMOLATE, 5);

		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE, target)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(415, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target),
				at(5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(8)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@Test
	void drainLife() {
		player.cast(DRAIN_LIFE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.beginCast(player, DRAIN_LIFE, target)
						.effectApplied(DRAIN_LIFE, target)
						.beginGcd(player),
				at(1)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(3)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(4)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(5)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE)
						.endCast(player, DRAIN_LIFE, target)
						.effectExpired(DRAIN_LIFE, target)
		);
	}

	@Nested
	class Misses {
		@Test
		void shadowBolt() {
			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.endCast(player, SHADOW_BOLT, target)
							.decreasedResource(420, MANA, player, SHADOW_BOLT)
							.spellMissed(player, SHADOW_BOLT, target)
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
							.spellMissed(player, SHADOWBURN, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test
		void lifeTap() {
			setMana(player, 0);

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(582);

			//can't miss on friendly spell

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP, player)
							.endCast(player, LIFE_TAP, player)
							.decreasedResource(582, HEALTH, player, LIFE_TAP)
							.increasedResource(582, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test
		void coa() {
			player.cast(CURSE_OF_AGONY);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CURSE_OF_AGONY, target)
							.endCast(player, CURSE_OF_AGONY, target)
							.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
							.spellMissed(player, CURSE_OF_AGONY, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test
		void corruption() {
			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.endCast(player, CORRUPTION, target)
							.decreasedResource(370, MANA, player, CORRUPTION)
							.spellMissed(player, CORRUPTION, target)
			);
		}

		@Test
		void drainLife() {
			player.cast(DRAIN_LIFE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.decreasedResource(425, MANA, player, DRAIN_LIFE)
							.beginCast(player, DRAIN_LIFE, target)
							.spellMissed(player, DRAIN_LIFE, target)
							.endCast(player, DRAIN_LIFE, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@BeforeEach
		void setUp() {
			rng.hitRoll = false;
		}
	}

	@Nested
	class Interruptions {
		@Test
		void shadowBolt() {
			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT, target)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, SHADOW_BOLT, target)
							.endGcd(player)
			);
		}

		@Test
		void shadowBurn() {
			enableTalent(TalentId.SHADOWBURN, 1);

			player.cast(SHADOWBURN);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOWBURN, target)
							.endCast(player, SHADOWBURN, target)
							.decreasedResource(515, MANA, player, SHADOWBURN)
							.decreasedResource(631, HEALTH, target, SHADOWBURN)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test
		void coa() {
			player.cast(CURSE_OF_AGONY);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(10));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CURSE_OF_AGONY, target)
							.endCast(player, CURSE_OF_AGONY, target)
							.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
							.effectApplied(CURSE_OF_AGONY, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(4)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(6)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(8)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(10)
							.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
					at(12)
							.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
					at(14)
							.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
					at(16)
							.decreasedResource(113, HEALTH, target, CURSE_OF_AGONY),
					at(18)
							.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
					at(20)
							.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
					at(22)
							.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
					at(24)
							.decreasedResource(173, HEALTH, target, CURSE_OF_AGONY)
							.effectExpired(CURSE_OF_AGONY, target)
			);
		}

		@Test
		void corruptionInterruptCast() {
			enableTalent(IMPROVED_CORRUPTION, 2);

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION, target)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, CORRUPTION, target)
							.endGcd(player)
			);
		}

		@Test
		void corruptionInterruptGcd() {
			enableTalent(IMPROVED_CORRUPTION, 2);

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(1.4));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION, target)
							.beginGcd(player),
					at(1.20)
							.endCast(player, CORRUPTION, target)
							.decreasedResource(370, MANA, player, CORRUPTION)
							.effectApplied(CORRUPTION, target),
					at(1.5)
							.endGcd(player),
					at(4.2)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(7.2)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(10.2)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(13.2)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(16.2)
							.decreasedResource(150, HEALTH, target, CORRUPTION),
					at(19.2)
							.decreasedResource(150, HEALTH, target, CORRUPTION)
							.effectExpired(CORRUPTION, target)
			);
		}

		@Test
		void immolate() {
			player.cast(IMMOLATE);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, IMMOLATE, target)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, IMMOLATE, target)
							.endGcd(player)
			);
		}

		@Test
		void drainLife() {
			player.cast(DRAIN_LIFE);

			simulation.updateUntil(Time.at(1.25));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.decreasedResource(425, MANA, player, DRAIN_LIFE)
							.beginCast(player, DRAIN_LIFE, target)
							.effectApplied(DRAIN_LIFE, target)
							.beginGcd(player),
					at(1)
							.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
							.increasedResource(108, HEALTH, player, DRAIN_LIFE),
					at(1.25)
							.castInterrupted(player, DRAIN_LIFE, target)
							.effectRemoved(DRAIN_LIFE, target)
							.endGcd(player)
			);
		}
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