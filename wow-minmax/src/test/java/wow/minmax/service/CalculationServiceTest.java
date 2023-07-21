package wow.minmax.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.Equipment;
import wow.character.model.snapshot.AccumulatedSpellStats;
import wow.character.model.snapshot.CritMode;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.ProcEventType;
import wow.commons.model.spells.Spell;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.RotationStats;
import wow.minmax.model.SpecialAbilityStats;
import wow.minmax.model.SpellStats;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static wow.commons.model.attributes.complex.special.ProcEventType.SPELL_DAMAGE;
import static wow.commons.model.attributes.complex.special.ProcEventType.*;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.buffs.BuffCategory.SELF_BUFF;
import static wow.commons.model.spells.SpellId.*;
import static wow.commons.model.spells.SpellSchool.FIRE;
import static wow.commons.model.spells.SpellSchool.SHADOW;
import static wow.minmax.service.CalculationService.EquivalentMode.ADDITIONAL;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class CalculationServiceTest extends ServiceTest {
	@Autowired
	CalculationService underTest;

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

	@DisplayName("Proc with internal cooldown")
	@ParameterizedTest(name = "[{index}] (event: {0}, chance: 50%, 100 sp | 15s/60s) == {1}, hit: {2}, crit: {3}, castTime: {4}")
	@MethodSource
	void procWithICD(ProcEventType event, double expected, double hitChance, double critChance, double castTime) {
		SpecialAbility proc = SpecialAbility.proc(
				event,
				Percent.of(50),
				Attributes.of(SPELL_POWER, 100),
				Duration.seconds(15),
				Duration.seconds(45),
				"test"
		);

		Snapshot snapshot = new Snapshot(null, character, null);
		snapshot.setHitChance(hitChance);
		snapshot.setCritChance(critChance);
		snapshot.setEffectiveCastTime(castTime);

		Attributes statEquivalent = underTest.getStatEquivalent(proc, snapshot);

		assertThat(statEquivalent.getSpellPower()).isEqualTo(expected, PRECISION);
	}

	static Stream<Arguments> procWithICD() {
		return Stream.of(
				arguments(SPELL_HIT, 31.10, 0.9, 0.2, 2),
				arguments(SPELL_HIT, 0, 0.9, 0.2, 20),
				arguments(SPELL_CRIT, 24.31, 0.9, 0.2, 2),
				arguments(SPELL_CRIT, 0, 0.9, 0.2, 20),
				arguments(SPELL_RESIST, 16.41, 0.91, 0.2, 2),
				arguments(SPELL_RESIST, 0, 0.91, 0.2, 20),
				arguments(SPELL_DAMAGE, 31.10, 0.9, 0.2, 2),
				arguments(SPELL_DAMAGE, 0, 0.9, 0.2, 20)
		);
	}

	@DisplayName("Proc without internal cooldown")
	@ParameterizedTest(name = "[{index}] (event: {0}, chance: 50%, 100 sp | 15s/60s) == {1}, hit: {2}, crit: {3}, castTime: {4}")
	@MethodSource
	void procWithoutICD(ProcEventType event, double expected, double hitChance, double critChance, double castTime) {
		SpecialAbility proc = SpecialAbility.proc(
				event,
				Percent.of(50),
				Attributes.of(SPELL_POWER, 100),
				Duration.seconds(15),
				Duration.ZERO,
				"test"
		);

		Snapshot snapshot = new Snapshot(null, character, null);
		snapshot.setHitChance(hitChance);
		snapshot.setCritChance(critChance);
		snapshot.setEffectiveCastTime(castTime);

		Attributes statEquivalent = underTest.getStatEquivalent(proc, snapshot);

		assertThat(statEquivalent.getSpellPower()).isEqualTo(expected, PRECISION);
	}

	static Stream<Arguments> procWithoutICD() {
		return Stream.of(
				arguments(SPELL_HIT, 93.49, 0.9, 0.2, 2),
				arguments(SPELL_HIT, 0, 0.9, 0.2, 20),
				arguments(SPELL_CRIT, 46.04, 0.9, 0.2, 2),
				arguments(SPELL_CRIT, 0, 0.9, 0.2, 20),
				arguments(SPELL_RESIST, 23.99, 0.91, 0.2, 2),
				arguments(SPELL_RESIST, 0, 0.9, 0.2, 20),
				arguments(SPELL_DAMAGE, 94.95, 0.9, 0.2, 2),
				arguments(SPELL_DAMAGE, 0, 0.9, 0.2, 20)
		);
	}

	@Test
	@DisplayName("OnUse")
	void onUse() {
		SpecialAbility onUse = SpecialAbility.onUse(
				Attributes.of(SPELL_POWER, 100),
				Duration.seconds(15),
				Duration.seconds(60),
				"test"
		);

		Snapshot snapshot = new Snapshot(null, character, null);

		Attributes statEquivalent = underTest.getStatEquivalent(onUse, snapshot);

		assertThat(statEquivalent.getSpellPower()).isEqualTo(25);
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

		assertThat(snapshot.getMaxHealth()).usingComparator(ROUNDED_DOWN).isEqualTo(11623);
		assertThat(snapshot.getMaxMana()).usingComparator(ROUNDED_DOWN).isEqualTo(11990);
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

	@Test
	@DisplayName("Correct stat quivalent")
	void correctStatEquivalent() {
		character.setEquipment(getEquipment());

		Attributes hitEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_HIT_RATING, 10), SPELL_POWER, ADDITIONAL, character);
		Attributes critEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_CRIT_RATING, 10), SPELL_POWER, ADDITIONAL, character);
		Attributes hasteEqv = underTest.getDpsStatEquivalent(Attributes.of(SPELL_HASTE_RATING, 10), SPELL_POWER, ADDITIONAL, character);

		assertThat(hitEqv.getSpellPower()).isEqualTo(0.11, PRECISION);
		assertThat(critEqv.getSpellPower()).isEqualTo(9.79, PRECISION);
		assertThat(hasteEqv.getSpellPower()).isEqualTo(11.37, PRECISION);
	}

	@Test
	@DisplayName("Correct stat quivalent")
	void correctAbilityEquivalent() {
		character.setEquipment(getEquipment());

		String line = "Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)";
		SpecialAbility specialAbility = character.getEquipment().getStats().getSpecialAbilities().stream()
				.filter(x -> line.equals(x.line()))
				.findFirst()
				.orElseThrow();
		Attributes equivalent = underTest.getAbilityEquivalent(specialAbility, character);

		assertThat(equivalent.getSpellHasteRating()).isEqualTo(29.17, PRECISION);
	}

	@Test
	@DisplayName("Correct rotation dps")
	void correctRotationDps() {
		character.setEquipment(getEquipment());

		double dps = underTest.getRotationDps(character, character.getRotation());

		assertThat(dps).usingComparator(ROUNDED_DOWN).isEqualTo(2777);
	}

	@Test
	@DisplayName("Correct rotation stats")
	void correctRotationStats() {
		character.setEquipment(getEquipment());

		RotationStats stats = underTest.getRotationStats(character, character.getRotation());

		assertThat(stats.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2777);
		assertThat(stats.getStatList()).hasSize(2);
		assertThat(stats.getStatList().get(0).getSpell().getSpellId()).isEqualTo(CURSE_OF_DOOM);
		assertThat(stats.getStatList().get(1).getSpell().getSpellId()).isEqualTo(SHADOW_BOLT);
	}

	@Test
	@DisplayName("Correct spell stats")
	void correctSpellStats() {
		character.setEquipment(getEquipment());

		SpellStats spellStats = underTest.getSpellStats(character, character.getRotation().getFiller());

		assertThat(spellStats.getCharacter()).isSameAs(character);
		assertThat(spellStats.getSpellStatistics().getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5175);
		assertThat(spellStats.getSpellStatistics().getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2669);
		assertThat(spellStats.getSpellStatistics().getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(spellStats.getSpellStatistics().getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(spellStats.getSpellStatistics().getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(12);
		assertThat(spellStats.getStatEquivalents().getHitSpEqv()).isEqualTo(0.11, PRECISION);
		assertThat(spellStats.getStatEquivalents().getCritSpEqv()).isEqualTo(10.15, PRECISION);
		assertThat(spellStats.getStatEquivalents().getHasteSpEqv()).isEqualTo(11.55, PRECISION);
	}

	@Test
	@DisplayName("Correct current stats")
	void correctCurrentStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getCurrentStats(character);

		assertThat(stats.getSp()).isEqualTo(1616);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1750.0);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1696.0);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(16.00, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(331);
		assertThat(stats.getCritPct()).isEqualTo(30.31, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(797);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(620);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(245);
	}

	@Test
	@DisplayName("Correct stats")
	void correctStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getStats(character, SELF_BUFF);

		assertThat(stats.getSp()).isEqualTo(1456);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1510.0);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1456.0);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(12.99, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(317);
		assertThat(stats.getCritPct()).isEqualTo(25.30, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(626);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(510);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(142);
	}

	@Test
	@DisplayName("Correct equipment stats")
	void correctEquipmentStats() {
		character.setEquipment(getEquipment());

		CharacterStats stats = underTest.getEquipmentStats(character);

		assertThat(stats.getSp()).isEqualTo(1326);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1380.0);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1326.0);
		assertThat(stats.getHitRating()).isEqualTo(164);
		assertThat(stats.getHitPct()).isEqualTo(13.00, PRECISION);
		assertThat(stats.getCritRating()).isEqualTo(317);
		assertThat(stats.getCritPct()).isEqualTo(22.30, PRECISION);
		assertThat(stats.getHasteRating()).isEqualTo(426);
		assertThat(stats.getHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(stats.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(545);
		assertThat(stats.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(510);
		assertThat(stats.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(150);
	}

	@Test
	@DisplayName("Correct special ability stats")
	void correctSpecialAbilityStats() {
		character.setEquipment(getEquipment());

		SpecialAbility specialAbility = character.getStats().getSpecialAbilities().stream()
				.filter(x -> "Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)".equals(x.line()))
				.findFirst()
				.orElseThrow();

		SpecialAbilityStats stats = underTest.getSpecialAbilityStats(character, specialAbility);

		assertThat(stats.getAbility()).isSameAs(specialAbility);
		assertThat(stats.getStatEquivalent().statString()).isEqualTo("29.17 spell haste rating");
		assertThat(stats.getSpEquivalent()).isEqualTo(33.62, PRECISION);
	}
}