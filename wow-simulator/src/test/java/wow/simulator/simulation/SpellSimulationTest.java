package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import wow.commons.model.buff.BuffId;
import wow.commons.model.talent.TalentId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.time.Time;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.SHADOWBURN;
import static wow.commons.model.spell.AbilityId.SIPHON_LIFE;
import static wow.commons.model.spell.AbilityId.UNSTABLE_AFFLICTION;
import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.*;

/**
 * User: POlszewski
 * Date: 2023-08-23
 */
class SpellSimulationTest extends WowSimulatorSpringTest {
	abstract static class SpellTestCases {
		abstract void success();

		abstract void resisted();

		abstract void interrupted();
	}

	@Nested
	class ShadowBolt extends SpellTestCases {
		@Test@Override
		void success() {
			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.endCast(player, SHADOW_BOLT)
							.decreasedResource(420, MANA, player, SHADOW_BOLT)
							.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.endCast(player, SHADOW_BOLT)
							.decreasedResource(420, MANA, player, SHADOW_BOLT)
							.spellResisted(player, SHADOW_BOLT, target)
			);
		}

		@Test@Override
		void interrupted() {
			player.cast(SHADOW_BOLT);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOW_BOLT)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, SHADOW_BOLT)
							.endGcd(player)
			);
		}

		@Nested
		class SpecialCases {
			@Test
			void bane() {
				enableTalent(BANE, 5);

				player.cast(SHADOW_BOLT);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, SHADOW_BOLT)
								.beginGcd(player),
						at(1.5)
								.endGcd(player),
						at(2.5)
								.endCast(player, SHADOW_BOLT)
								.decreasedResource(420, MANA, player, SHADOW_BOLT)
								.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
				);
			}

			@Test
			void cataclysm() {
				enableTalent(CATACLYSM, 5);

				player.cast(SHADOW_BOLT);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, SHADOW_BOLT)
								.beginGcd(player),
						at(1.5)
								.endGcd(player),
						at(3)
								.endCast(player, SHADOW_BOLT)
								.decreasedResource(399, MANA, player, SHADOW_BOLT)
								.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
				);
			}
		}
	}

	@Nested
	class ShadowBurn extends SpellTestCases {
		@Test@Override
		void success() {
			enableTalent(TalentId.SHADOWBURN, 1);

			player.cast(SHADOWBURN);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOWBURN)
							.endCast(player, SHADOWBURN)
							.decreasedResource(515, MANA, player, SHADOWBURN)
							.cooldownStarted(player, SHADOWBURN)
							.decreasedResource(631, HEALTH, target, SHADOWBURN)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(15)
							.cooldownExpired(player, SHADOWBURN)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			enableTalent(TalentId.SHADOWBURN, 1);

			player.cast(SHADOWBURN);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOWBURN)
							.endCast(player, SHADOWBURN)
							.decreasedResource(515, MANA, player, SHADOWBURN)
							.cooldownStarted(player, SHADOWBURN)
							.spellResisted(player, SHADOWBURN, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(15)
							.cooldownExpired(player, SHADOWBURN)
			);
		}

		@Test@Override
		void interrupted() {
			enableTalent(TalentId.SHADOWBURN, 1);

			player.cast(SHADOWBURN);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SHADOWBURN)
							.endCast(player, SHADOWBURN)
							.decreasedResource(515, MANA, player, SHADOWBURN)
							.cooldownStarted(player, SHADOWBURN)
							.decreasedResource(631, HEALTH, target, SHADOWBURN)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(15)
							.cooldownExpired(player, SHADOWBURN)
			);
		}
	}

	@Nested
	class CurseOfAgony extends SpellTestCases {
		@Test@Override
		void success() {
			player.cast(CURSE_OF_AGONY);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CURSE_OF_AGONY)
							.endCast(player, CURSE_OF_AGONY)
							.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
							.effectApplied(CURSE_OF_AGONY, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(4)
							.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
					at(6)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(8)
							.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
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
							.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY),
					at(22)
							.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
					at(24)
							.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY)
							.effectExpired(CURSE_OF_AGONY, target)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			player.cast(CURSE_OF_AGONY);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CURSE_OF_AGONY)
							.endCast(player, CURSE_OF_AGONY)
							.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
							.spellResisted(player, CURSE_OF_AGONY, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test@Override
		void interrupted() {
			player.cast(CURSE_OF_AGONY);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(10));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CURSE_OF_AGONY)
							.endCast(player, CURSE_OF_AGONY)
							.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
							.effectApplied(CURSE_OF_AGONY, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(4)
							.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
					at(6)
							.decreasedResource(56, HEALTH, target, CURSE_OF_AGONY),
					at(8)
							.decreasedResource(57, HEALTH, target, CURSE_OF_AGONY),
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
							.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY),
					at(22)
							.decreasedResource(169, HEALTH, target, CURSE_OF_AGONY),
					at(24)
							.decreasedResource(170, HEALTH, target, CURSE_OF_AGONY)
							.effectExpired(CURSE_OF_AGONY, target)
			);
		}

		@Nested
		class SpecialCases {
			@Test
			void improvedCurseOfAgony() {
				enableTalent(IMPROVED_CURSE_OF_AGONY, 2);

				player.cast(CURSE_OF_AGONY);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, CURSE_OF_AGONY)
								.endCast(player, CURSE_OF_AGONY)
								.decreasedResource(265, MANA, player, CURSE_OF_AGONY)
								.effectApplied(CURSE_OF_AGONY, target)
								.beginGcd(player),
						at(1.5)
								.endGcd(player),
						at(2)
								.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
						at(4)
								.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
						at(6)
								.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
						at(8)
								.decreasedResource(62, HEALTH, target, CURSE_OF_AGONY),
						at(10)
								.decreasedResource(124, HEALTH, target, CURSE_OF_AGONY),
						at(12)
								.decreasedResource(125, HEALTH, target, CURSE_OF_AGONY),
						at(14)
								.decreasedResource(124, HEALTH, target, CURSE_OF_AGONY),
						at(16)
								.decreasedResource(124, HEALTH, target, CURSE_OF_AGONY),
						at(18)
								.decreasedResource(187, HEALTH, target, CURSE_OF_AGONY),
						at(20)
								.decreasedResource(186, HEALTH, target, CURSE_OF_AGONY),
						at(22)
								.decreasedResource(187, HEALTH, target, CURSE_OF_AGONY),
						at(24)
								.decreasedResource(186, HEALTH, target, CURSE_OF_AGONY)
								.effectExpired(CURSE_OF_AGONY, target)
				);
			}
		}
	}

	@Nested
	class Corruption extends SpellTestCases {
		@Test@Override
		void success() {
			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.endCast(player, CORRUPTION)
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

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.endCast(player, CORRUPTION)
							.decreasedResource(370, MANA, player, CORRUPTION)
							.spellResisted(player, CORRUPTION, target)
			);
		}

		@Test@Override
		void interrupted() {
			enableTalent(IMPROVED_CORRUPTION, 2);

			player.cast(CORRUPTION);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, CORRUPTION)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, CORRUPTION)
							.endGcd(player)
			);
		}

		@Nested
		class SpecialCases {
			@Test
			void improvedCorruption() {
				enableTalent(IMPROVED_CORRUPTION, 5);

				player.cast(CORRUPTION);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, CORRUPTION)
								.endCast(player, CORRUPTION)
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
			void empoweredCorruption() {
				enableTalent(IMPROVED_CORRUPTION, 5);
				enableTalent(EMPOWERED_CORRUPTION, 3);
				enableBuff(BuffId.FEL_ARMOR, 2);

				player.cast(CORRUPTION);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, CORRUPTION)
								.endCast(player, CORRUPTION)
								.decreasedResource(370, MANA, player, CORRUPTION)
								.effectApplied(CORRUPTION, target)
								.beginGcd(player),
						at(1.5)
								.endGcd(player),
						at(3)
								.decreasedResource(171, HEALTH, target, CORRUPTION),
						at(6)
								.decreasedResource(172, HEALTH, target, CORRUPTION),
						at(9)
								.decreasedResource(171, HEALTH, target, CORRUPTION),
						at(12)
								.decreasedResource(172, HEALTH, target, CORRUPTION),
						at(15)
								.decreasedResource(171, HEALTH, target, CORRUPTION),
						at(18)
								.decreasedResource(172, HEALTH, target, CORRUPTION)
								.effectExpired(CORRUPTION, target)
				);
			}

			@Test
			void t5Bonus() {
				enableTalent(IMPROVED_CORRUPTION, 5);

				equip("Voidheart Crown");
				equip("Voidheart Gloves");
				equip("Voidheart Leggings");
				equip("Voidheart Mantle");

				player.cast(CORRUPTION);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, CORRUPTION)
								.endCast(player, CORRUPTION)
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
		}
	}

	@Nested
	class Immolate extends SpellTestCases {
		@Test@Override
		void success() {
			player.cast(IMMOLATE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, IMMOLATE)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.endCast(player, IMMOLATE)
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

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			player.cast(IMMOLATE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, IMMOLATE)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(2)
							.endCast(player, IMMOLATE)
							.decreasedResource(445, MANA, player, IMMOLATE)
							.spellResisted(player, IMMOLATE, target)
			);
		}

		@Test@Override
		void interrupted() {
			player.cast(IMMOLATE);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, IMMOLATE)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, IMMOLATE)
							.endGcd(player)
			);
		}

		@Nested
		class SpecialCases {
			@Test
			void bane() {
				enableTalent(BANE, 5);

				player.cast(IMMOLATE);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, IMMOLATE)
								.beginGcd(player),
						at(1.5)
								.endCast(player, IMMOLATE)
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
			void improvedImmolate() {
				enableTalent(IMPROVED_IMMOLATE, 5);

				player.cast(IMMOLATE);

				simulation.updateUntil(Time.at(30));

				assertEvents(
						at(0)
								.beginCast(player, IMMOLATE)
								.beginGcd(player),
						at(1.5)
								.endGcd(player),
						at(2)
								.endCast(player, IMMOLATE)
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
		}
	}

	@Nested
	class UnstableAffliction extends SpellTestCases {
		@Test@Override
		void success() {
			enableTalent(TalentId.UNSTABLE_AFFLICTION, 1);

			player.cast(UNSTABLE_AFFLICTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, UNSTABLE_AFFLICTION)
							.beginGcd(player),
					at(1.5)
							.endCast(player, UNSTABLE_AFFLICTION)
							.decreasedResource(400, MANA, player, UNSTABLE_AFFLICTION)
							.effectApplied(UNSTABLE_AFFLICTION, target)
							.endGcd(player),
					at(4.5)
							.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
					at(7.5)
							.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
					at(10.5)
							.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
					at(13.5)
							.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
					at(16.5)
							.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION),
					at(19.5)
							.decreasedResource(175, HEALTH, target, UNSTABLE_AFFLICTION)
							.effectExpired(UNSTABLE_AFFLICTION, target)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			enableTalent(TalentId.UNSTABLE_AFFLICTION, 1);

			player.cast(UNSTABLE_AFFLICTION);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, UNSTABLE_AFFLICTION)
							.beginGcd(player),
					at(1.5)
							.endCast(player, UNSTABLE_AFFLICTION)
							.decreasedResource(400, MANA, player, UNSTABLE_AFFLICTION)
							.spellResisted(player, UNSTABLE_AFFLICTION, target)
							.endGcd(player)
			);
		}

		@Test@Override
		void interrupted() {
			enableTalent(TalentId.UNSTABLE_AFFLICTION, 1);

			player.cast(UNSTABLE_AFFLICTION);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, UNSTABLE_AFFLICTION)
							.beginGcd(player),
					at(1)
							.castInterrupted(player, UNSTABLE_AFFLICTION)
							.endGcd(player)
			);
		}
	}

	@Nested
	class SiphonLife extends SpellTestCases {
		@Test@Override
		void success() {
			enableTalent(TalentId.SIPHON_LIFE, 1);

			player.cast(SIPHON_LIFE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SIPHON_LIFE)
							.endCast(player, SIPHON_LIFE)
							.decreasedResource(410, MANA, player, SIPHON_LIFE)
							.effectApplied(SIPHON_LIFE, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(6)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(9)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(12)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(15)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(18)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(21)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(24)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(27)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(30)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE)
							.effectExpired(SIPHON_LIFE, target)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			enableTalent(TalentId.SIPHON_LIFE, 1);

			player.cast(SIPHON_LIFE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SIPHON_LIFE)
							.endCast(player, SIPHON_LIFE)
							.decreasedResource(410, MANA, player, SIPHON_LIFE)
							.spellResisted(player, SIPHON_LIFE, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test@Override
		void interrupted() {
			enableTalent(TalentId.SIPHON_LIFE, 1);

			player.cast(SIPHON_LIFE);

			simulation.updateUntil(Time.at(1));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, SIPHON_LIFE)
							.endCast(player, SIPHON_LIFE)
							.decreasedResource(410, MANA, player, SIPHON_LIFE)
							.effectApplied(SIPHON_LIFE, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player),
					at(3)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(6)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(9)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(12)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(15)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(18)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(21)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(24)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(27)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE),
					at(30)
							.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
							.increasedResource(63, HEALTH, player, SIPHON_LIFE)
							.effectExpired(SIPHON_LIFE, target)
			);
		}
	}

	@Nested
	class DrainLife extends SpellTestCases {
		@Test@Override
		void success() {
			player.cast(DRAIN_LIFE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, DRAIN_LIFE)
							.endCast(player, DRAIN_LIFE)
							.decreasedResource(425, MANA, player, DRAIN_LIFE)
							.beginChannel(player, DRAIN_LIFE)
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
							.effectExpired(DRAIN_LIFE, target)
							.endChannel(player, DRAIN_LIFE)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			player.cast(DRAIN_LIFE);

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, DRAIN_LIFE)
							.endCast(player, DRAIN_LIFE)
							.decreasedResource(425, MANA, player, DRAIN_LIFE)
							.spellResisted(player, DRAIN_LIFE, target)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test@Override
		void interrupted() {
			player.cast(DRAIN_LIFE);

			simulation.updateUntil(Time.at(1.25));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertEvents(
					at(0)
							.beginCast(player, DRAIN_LIFE)
							.endCast(player, DRAIN_LIFE)
							.decreasedResource(425, MANA, player, DRAIN_LIFE)
							.beginChannel(player, DRAIN_LIFE)
							.effectApplied(DRAIN_LIFE, target)
							.beginGcd(player),
					at(1)
							.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
							.increasedResource(108, HEALTH, player, DRAIN_LIFE),
					at(1.25)
							.channelInterrupted(player, DRAIN_LIFE)
							.effectRemoved(DRAIN_LIFE, target)
							.endGcd(player)
			);
		}
	}

	@Nested
	class LifeTap extends SpellTestCases {
		@Test@Override
		void success() {
			setMana(player, 0);

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(582);

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP)
							.endCast(player, LIFE_TAP)
							.decreasedResource(582, HEALTH, player, LIFE_TAP)
							.increasedResource(582, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test@Override
		void resisted() {
			rng.hitRoll = false;

			setMana(player, 0);

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(582);

			//can't resist friendly spell

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP)
							.endCast(player, LIFE_TAP)
							.decreasedResource(582, HEALTH, player, LIFE_TAP)
							.increasedResource(582, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Test@Override
		void interrupted() {
			setMana(player, 0);

			player.cast(LIFE_TAP);

			simulation.updateUntil(Time.at(1.25));

			player.interruptCurrentAction();

			simulation.updateUntil(Time.at(30));

			assertThat(player.getCurrentMana()).isEqualTo(582);

			assertEvents(
					at(0)
							.beginCast(player, LIFE_TAP)
							.endCast(player, LIFE_TAP)
							.decreasedResource(582, HEALTH, player, LIFE_TAP)
							.increasedResource(582, MANA, player, LIFE_TAP)
							.beginGcd(player),
					at(1.5)
							.endGcd(player)
			);
		}

		@Nested
		class SpecialCases {
			@Test
			void improvedLifeTap() {
				enableTalent(IMPROVED_LIFE_TAP, 2);
				setMana(player, 0);

				player.cast(LIFE_TAP);

				simulation.updateUntil(Time.at(30));

				assertThat(player.getCurrentMana()).isEqualTo(698);

				assertEvents(
						at(0)
								.beginCast(player, LIFE_TAP)
								.endCast(player, LIFE_TAP)
								.decreasedResource(698, HEALTH, player, LIFE_TAP)
								.increasedResource(698, MANA, player, LIFE_TAP)
								.beginGcd(player),
						at(1.5)
								.endGcd(player)
				);
			}

			@Test
			void improvedLifeTapAndSp() {
				enableTalent(IMPROVED_LIFE_TAP, 2);
				setMana(player, 0);

				equip("Tempest of Chaos");

				player.cast(LIFE_TAP);

				simulation.updateUntil(Time.at(30));

				assertThat(player.getCurrentMana()).isEqualTo(947);// (582 + 0.8 * 259) * 1.2

				assertEvents(
						at(0)
								.beginCast(player, LIFE_TAP)
								.endCast(player, LIFE_TAP)
								.decreasedResource(947, HEALTH, player, LIFE_TAP)
								.increasedResource(947, MANA, player, LIFE_TAP)
								.beginGcd(player),
						at(1.5)
								.endGcd(player)
				);
			}

			@Test
			void t3Bonus() {
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

				assertThat(player.getCurrentMana()).isEqualTo(959);// (582 + 0.8*272) * 1.2

				assertEvents(
						at(0)
								.beginCast(player, LIFE_TAP)
								.endCast(player, LIFE_TAP)
								.decreasedResource(843, HEALTH, player, LIFE_TAP)
								.increasedResource(959, MANA, player, LIFE_TAP)
								.beginGcd(player),
						at(1.5)
								.endGcd(player)
				);
			}
		}
	}

	@BeforeEach
	void setUp() {
		setupTestObjects();

		handler = new WowSimulatorSpringTest.EventCollectingHandler();

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);
	}
}
