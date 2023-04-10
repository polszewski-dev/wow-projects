package wow.character.model.snapshot;

import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.spells.SpellId.*;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class SnapshotTest extends WowCharacterSpringTest {
	@Test
	void everytingAcummulatedStatsBaseStats() {
		Snapshot snapshot = getSnapshot();

		AccumulatedSpellStats underTest = snapshot.getStats();

		assertThat(underTest.getBaseStatsPct()).isEqualTo(10);
		assertThat(underTest.getStaminaPct()).isEqualTo(15);
		assertThat(underTest.getSpiritPct()).isEqualTo(-5);
		assertThat(underTest.getStamina()).isEqualTo(618);
		assertThat(underTest.getIntellect()).isEqualTo(544);
		assertThat(underTest.getSpirit()).isEqualTo(214);
		assertThat(underTest.getBaseStats()).isEqualTo(20);
	}

	@Test
	void everytingAcummulatedStatsSpellStats() {
		Snapshot snapshot = getSnapshot();

		AccumulatedSpellStats underTest = snapshot.getStats();

		assertThat(underTest.getSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(underTest.getDamagePct()).isEqualTo(31);
		assertThat(underTest.getHitRating()).isEqualTo(164);
		assertThat(underTest.getHitPct()).isEqualTo(3);
		assertThat(underTest.getCritRating()).isEqualTo(331);
		assertThat(underTest.getCritPct()).isEqualTo(14.3);
		assertThat(underTest.getHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(455);
		assertThat(underTest.getCritDamagePct()).isEqualTo(3);
		assertThat(underTest.getCritDamageMultiplierPct()).isEqualTo(100);
		assertThat(underTest.getCritCoeffPct()).isEqualTo(0.63, PRECISION);
		assertThat(underTest.getSpellPowerCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTime()).isEqualTo(-0.5);
		assertThat(underTest.getCostPct()).isEqualTo(-5);
		assertThat(underTest.getDuration()).isZero();
		assertThat(underTest.getDurationPct()).isZero();
		assertThat(underTest.getCooldown()).isZero();
		assertThat(underTest.getCooldownPct()).isZero();
		assertThat(underTest.getThreatPct()).isEqualTo(-12);
		assertThat(underTest.getPushbackPct()).isEqualTo(-70);
	}

	@Test
	void everythingSnapshot() {
		Snapshot underTest = getSnapshot();

		assertThat(underTest.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(797);
		assertThat(underTest.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(620);
		assertThat(underTest.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(245);

		assertThat(underTest.getTotalCrit()).isEqualTo(35.31, PRECISION);
		assertThat(underTest.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(underTest.getTotalHaste()).isEqualTo(28.88, PRECISION);

		assertThat(underTest.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(underTest.getSpMultiplier()).isEqualTo(1);

		assertThat(underTest.getHitChance()).isEqualTo(0.9899, PRECISION);
		assertThat(underTest.getCritChance()).isEqualTo(0.3530, PRECISION);
		assertThat(underTest.getCritCoeff()).isEqualTo(2.72, PRECISION);
		assertThat(underTest.getHaste()).isEqualTo(0.2888, PRECISION);

		assertThat(underTest.getDirectDamageDoneMultiplier()).isEqualTo(1.31);
		assertThat(underTest.getDotDamageDoneMultiplier()).isEqualTo(1.31);

		assertThat(underTest.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(underTest.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(underTest.getCastTime()).isEqualTo(1.94, PRECISION);
		assertThat(underTest.getGcd()).isEqualTo(1.16, PRECISION);
		assertThat(underTest.getEffectiveCastTime()).isEqualTo(1.94, PRECISION);
		assertThat(underTest.isInstantCast()).isFalse();

		assertThat(underTest.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);

		assertThat(underTest.getDuration()).isZero();
		assertThat(underTest.getCooldown()).isZero();
		assertThat(underTest.getThreatPct()).isEqualTo(88);
		assertThat(underTest.getPushbackPct()).isEqualTo(30);
	}

	@Test
	void testDotsHaveNoCrit() {
		Spell spell = getSpell(CORRUPTION);
		Character character = getCharacter();

		Attributes attributes = Attributes.of();

		Snapshot underTest = new Snapshot(spell, character, attributes);

		assertThat(underTest.getTotalCrit()).isZero();
		assertThat(underTest.getCritChance()).isZero();
		assertThat(underTest.getCritCoeff()).isZero();
		assertThat(underTest.getSpellCoeffDirect()).isZero();
	}

	@Test
	void everythingSpellStats() {
		Snapshot snapshot = getSnapshot();
		SpellStatistics underTest = snapshot.getSpellStatistics(CritMode.AVERAGE, true);

		assertThat(underTest.getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5175);
		assertThat(underTest.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2669);
		assertThat(underTest.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(underTest.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(underTest.getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(12);
	}

	@Test
	void testCastTimeUnmodified() {
		Spell spell = getSpell(CORRUPTION);
		Character character = getCharacter();

		Attributes attributes = Attributes.of();

		Snapshot underTest = new Snapshot(spell, character, attributes);

		assertThat(underTest.getCastTime()).isEqualTo(2.0);
		assertThat(underTest.getGcd()).isEqualTo(1.5);
		assertThat(underTest.getEffectiveCastTime()).isEqualTo(2.0);
		assertThat(underTest.isInstantCast()).isFalse();
	}

	@Test
	void testCastTimeReductionBelowGcd() {
		Spell spell = getSpell(CORRUPTION);
		Character character = getCharacter();

		Attributes attributes = Attributes.of(
				CAST_TIME, -1.0
		);

		Snapshot underTest = new Snapshot(spell, character, attributes);

		assertThat(underTest.getCastTime()).isEqualTo(1.0);
		assertThat(underTest.getGcd()).isEqualTo(1.5);
		assertThat(underTest.getEffectiveCastTime()).isEqualTo(1.5);
		assertThat(underTest.isInstantCast()).isFalse();
	}

	@Test
	void testCastTimeReductionToInstant() {
		Spell spell = getSpell(CORRUPTION);
		Character character = getCharacter();

		Attributes attributes = Attributes.of(
				CAST_TIME, -2.0
		);

		Snapshot underTest = new Snapshot(spell, character, attributes);

		assertThat(underTest.getCastTime()).isZero();
		assertThat(underTest.getGcd()).isEqualTo(1.5);
		assertThat(underTest.getEffectiveCastTime()).isEqualTo(1.5);
		assertThat(underTest.isInstantCast()).isTrue();
	}

	@Test
	void testDurationUnmodified() {
		Spell spell = getSpell(CORRUPTION);
		Character character = getCharacter();

		Attributes attributes = Attributes.of();

		Snapshot underTest = new Snapshot(spell, character, attributes);
		SpellStatistics stats = underTest.getSpellStatistics(CritMode.NEVER, false);

		assertThat(underTest.getDuration()).isEqualTo(18);
		assertThat(stats.getTotalDamage()).isEqualTo(747);
	}

	@Test
	void testDurationIncreased() {
		Spell spell = getSpell(CORRUPTION);
		Character character = getCharacter();

		Attributes attributes = Attributes.of(
				DURATION, 9
		);

		Snapshot underTest = new Snapshot(spell, character, attributes);
		SpellStatistics stats = underTest.getSpellStatistics(CritMode.NEVER, false);

		assertThat(underTest.getDuration()).isEqualTo(27);
		assertThat(stats.getTotalDamage()).isEqualTo(747 * 1.5);
	}

	@Test
	void testCooldownUnmodified() {
		Spell spell = getSpell(CURSE_OF_DOOM);
		Character character = getCharacter();

		Attributes attributes = Attributes.of();

		Snapshot underTest = new Snapshot(spell, character, attributes);

		assertThat(underTest.getCooldown()).isEqualTo(60);
	}

	@Test
	void testCooldownIncreased() {
		Spell spell = getSpell(CURSE_OF_DOOM);
		Character character = getCharacter();

		Attributes attributes = Attributes.of(
				COOLDOWN, 30
		);

		Snapshot underTest = new Snapshot(spell, character, attributes);

		assertThat(underTest.getCooldown()).isEqualTo(90);
	}

	private Snapshot getSnapshot() {
		Spell spell = getSpell(SHADOW_BOLT);
		Character character = getCharacter();

		character.setEquipment(getEquipment());

		Attributes everything = AttributeEvaluator.of()
				.addAttributes(character)
				.solveAllLeaveAbilities();

		return new Snapshot(spell, character, everything);
	}
}