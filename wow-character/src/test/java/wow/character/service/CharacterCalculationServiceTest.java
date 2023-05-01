package wow.character.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.model.equipment.Equipment;
import wow.character.model.snapshot.AccumulatedSpellStats;
import wow.character.model.snapshot.CritMode;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.spells.SpellId.*;

/**
 * User: POlszewski
 * Date: 2023-04-28
 */
class CharacterCalculationServiceTest extends WowCharacterSpringTest {
	@Autowired
	CharacterCalculationService underTest;

	@Test
	void testDotsHaveNoCrit() {
		Spell spell = getSpell(CORRUPTION);
		Attributes attributes = Attributes.of();

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);

		assertThat(snapshot.getTotalCrit()).isZero();
		assertThat(snapshot.getCritChance()).isZero();
		assertThat(snapshot.getCritCoeff()).isZero();
		assertThat(snapshot.getSpellCoeffDirect()).isZero();
	}


	@Test
	void testCastTimeUnmodified() {
		Spell spell = getSpell(CORRUPTION);
		Attributes attributes = Attributes.of();

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(2.0);
		assertThat(snapshot.isInstantCast()).isFalse();
	}

	@Test
	void testCastTimeReductionBelowGcd() {
		Spell spell = getSpell(CORRUPTION);
		Attributes attributes = Attributes.of(
				CAST_TIME, -1.0
		);

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(1.5);
		assertThat(snapshot.isInstantCast()).isFalse();
	}

	@Test
	void testCastTimeReductionToInstant() {
		Spell spell = getSpell(CORRUPTION);
		Attributes attributes = Attributes.of(
				CAST_TIME, -2.0
		);

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(1.5);
		assertThat(snapshot.isInstantCast()).isTrue();
	}

	@Test
	void testDurationUnmodified() {
		Spell spell = getSpell(CORRUPTION);
		Attributes attributes = Attributes.of();

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);
		SpellStatistics stats = snapshot.getSpellStatistics(CritMode.NEVER, false);

		assertThat(snapshot.getDuration()).isEqualTo(18);
		assertThat(stats.getTotalDamage()).isEqualTo(747);
	}

	@Test
	void testDurationIncreased() {
		Spell spell = getSpell(CORRUPTION);
		Attributes attributes = Attributes.of(
				DURATION, 9
		);

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);
		SpellStatistics stats = snapshot.getSpellStatistics(CritMode.NEVER, false);

		assertThat(snapshot.getDuration()).isEqualTo(27);
		assertThat(stats.getTotalDamage()).isEqualTo(747 * 1.5);
	}

	@Test
	void testCooldownUnmodified() {
		Spell spell = getSpell(CURSE_OF_DOOM);
		Attributes attributes = Attributes.of();

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);

		assertThat(snapshot.getCooldown()).isEqualTo(60);
	}

	@Test
	void testCooldownIncreased() {
		Spell spell = getSpell(CURSE_OF_DOOM);
		Attributes attributes = Attributes.of(
				COOLDOWN, 30
		);

		Snapshot snapshot = underTest.getSnapshot(character, spell, attributes);

		assertThat(snapshot.getCooldown()).isEqualTo(90);
	}

	@Test
	void everytingAcummulatedStatsBaseStats() {
		Snapshot snapshot = getSnapshot();

		AccumulatedSpellStats stats = snapshot.getStats();

		assertThat(stats.getBaseStatsPct()).isEqualTo(10);
		assertThat(stats.getStaminaPct()).isEqualTo(15);
		assertThat(stats.getSpiritPct()).isEqualTo(-5);
		assertThat(stats.getStamina()).isEqualTo(618);
		assertThat(stats.getIntellect()).isEqualTo(544);
		assertThat(stats.getSpirit()).isEqualTo(214);
		assertThat(stats.getBaseStats()).isEqualTo(20);
	}

	@Test
	void everytingAcummulatedStatsSpellStats() {
		Snapshot snapshot = getSnapshot();

		AccumulatedSpellStats stats = snapshot.getStats();

		assertThat(stats.getSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(stats.getDamagePct()).isEqualTo(31);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(3);
		assertThat(stats.getCritRating()).isEqualTo(331);
		assertThat(stats.getCritPct()).isEqualTo(14.3);
		assertThat(stats.getHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(455);
		assertThat(stats.getCritDamagePct()).isEqualTo(3);
		assertThat(stats.getCritDamageMultiplierPct()).isEqualTo(100);
		assertThat(stats.getCritCoeffPct()).isEqualTo(0.63, PRECISION);
		assertThat(stats.getSpellPowerCoeffPct()).isEqualTo(20);
		assertThat(stats.getCastTime()).isEqualTo(-0.5);
		assertThat(stats.getCostPct()).isEqualTo(-5);
		assertThat(stats.getDuration()).isZero();
		assertThat(stats.getDurationPct()).isZero();
		assertThat(stats.getCooldown()).isZero();
		assertThat(stats.getCooldownPct()).isZero();
		assertThat(stats.getThreatPct()).isEqualTo(-12);
		assertThat(stats.getPushbackPct()).isEqualTo(-70);
	}

	@Test
	void everythingSnapshot() {
		Snapshot snapshot = getSnapshot();

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(797);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(620);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(245);

		assertThat(snapshot.getTotalCrit()).isEqualTo(35.31, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(28.88, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1);

		assertThat(snapshot.getHitChance()).isEqualTo(0.9899, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.3530, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.72, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0.2888, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.31);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.31);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(1.94, PRECISION);
		assertThat(snapshot.isInstantCast()).isFalse();

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);

		assertThat(snapshot.getDuration()).isZero();
		assertThat(snapshot.getCooldown()).isZero();
		assertThat(snapshot.getThreatPct()).isEqualTo(88);
		assertThat(snapshot.getPushbackPct()).isEqualTo(30);
	}

	@Test
	void everythingSpellStats() {
		Snapshot snapshot = getSnapshot();
		SpellStatistics statistics = snapshot.getSpellStatistics(CritMode.AVERAGE, true);

		assertThat(statistics.getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5175);
		assertThat(statistics.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2669);
		assertThat(statistics.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(statistics.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(statistics.getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(12);
	}

	Snapshot getSnapshot() {
		Spell spell = getSpell(SHADOW_BOLT);
		character.setEquipment(getEquipment());
		return underTest.getSnapshot(character, spell, character.getStats());
	}

	@Test
	@DisplayName("Has talents, no buffs, no items")
	void hasTalentsNoBuffsNoItems() {
		character.setEquipment(new Equipment());
		character.resetBuffs();
		character.getTargetEnemy().resetDebuffs();

		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(88);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(131);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(136);

		assertThat(snapshot.getTotalCrit()).isEqualTo(11.29, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(0, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(0);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.83, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.11, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, no items")
	void hasTalentsHasBuffsNoItems() {
		character.setEquipment(new Equipment());

		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(212);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(203);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(239);

		assertThat(snapshot.getTotalCrit()).isEqualTo(15.83, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(3, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(370);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.86, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.15, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.72, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.25, PRECISION);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(2.5, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	@DisplayName("Has talents, has buffs, has items")
	void hasTalentsHasBuffsHasItems() {
		character.setEquipment(getEquipment());

		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.getSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(797);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(620);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(245);

		assertThat(snapshot.getTotalCrit()).isEqualTo(35.31, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(28.88, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.99, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.36, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2.72, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0.2888, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1.31, PRECISION);

		assertThat(snapshot.getEffectiveCastTime()).isEqualTo(1.94, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	Character character;

	@BeforeEach
	void setup() {
		character = getCharacter();
	}
}