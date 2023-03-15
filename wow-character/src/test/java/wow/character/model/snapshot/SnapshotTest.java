package wow.character.model.snapshot;

import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class SnapshotTest extends WowCharacterSpringTest {
	@Test
	void everytingAcummulatedStats() {
		Snapshot snapshot = getSnapshot();

		AccumulatedSpellStats underTest = snapshot.getStats();

		assertThat(underTest.getBaseStatsPct()).isEqualTo(10);
		assertThat(underTest.getStaminaPct()).isEqualTo(15);
		assertThat(underTest.getSpiritPct()).isEqualTo(-5);
		assertThat(underTest.getStamina()).isEqualTo(462);
		assertThat(underTest.getIntellect()).isEqualTo(413);
		assertThat(underTest.getSpirit()).isEqualTo(20);
		assertThat(underTest.getBaseStats()).isEqualTo(20);
		assertThat(underTest.getSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(underTest.getDamagePct()).isEqualTo(31);
		assertThat(underTest.getHitRating()).isEqualTo(164);
		assertThat(underTest.getHitPct()).isEqualTo(3);
		assertThat(underTest.getCritRating()).isEqualTo(331);
		assertThat(underTest.getCritPct()).isEqualTo(11);
		assertThat(underTest.getHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(455);
		assertThat(underTest.getCritDamagePct()).isEqualTo(3);
		assertThat(underTest.getCritDamageMultiplierPct()).isEqualTo(100);
		assertThat(underTest.getCritCoeffPct()).isEqualTo(0.67, PRECISION);
		assertThat(underTest.getSpellPowerCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTime()).isEqualTo(-0.5);
		assertThat(underTest.getCostPct()).isEqualTo(-5);
	}

	@Test
	void everythingSnapshot() {
		Snapshot underTest = getSnapshot();

		assertThat(underTest.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(700);
		assertThat(underTest.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(619);
		assertThat(underTest.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(161);

		assertThat(underTest.getTotalCrit()).isEqualTo(35.30, PRECISION);
		assertThat(underTest.getTotalHit()).isEqualTo(15.99, PRECISION);
		assertThat(underTest.getTotalHaste()).isEqualTo(28.88, PRECISION);

		assertThat(underTest.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(underTest.getSpMultiplier()).isEqualTo(1);

		assertThat(underTest.getHitChance()).isEqualTo(0.9899, PRECISION);
		assertThat(underTest.getCritChance()).isEqualTo(0.3530, PRECISION);
		assertThat(underTest.getCritCoeff()).isEqualTo(2.76, PRECISION);
		assertThat(underTest.getHaste()).isEqualTo(0.2888, PRECISION);

		assertThat(underTest.getDirectDamageDoneMultiplier()).isEqualTo(1.31);
		assertThat(underTest.getDotDamageDoneMultiplier()).isEqualTo(1.31);

		assertThat(underTest.getSpellCoeffDirect()).isEqualTo(1.0571, PRECISION);
		assertThat(underTest.getSpellCoeffDoT()).isEqualTo(0.2, PRECISION);

		assertThat(underTest.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(underTest.getGcd().getSeconds()).isEqualTo(1.16, PRECISION);
		assertThat(underTest.getEffectiveCastTime().getSeconds()).isEqualTo(1.94, PRECISION);

		assertThat(underTest.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
	}

	@Test
	void everythingSpellStats() {
		Snapshot snapshot = getSnapshot();
		SpellStatistics underTest = snapshot.getSpellStatistics(CritMode.AVERAGE, true);

		assertThat(underTest.getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5219);
		assertThat(underTest.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2691);
		assertThat(underTest.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(underTest.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(underTest.getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(13);
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