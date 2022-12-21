package wow.character.model.snapshot;

import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.model.character.Enemy;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_DAMAGE;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class SnapshotTest extends WowCharacterSpringTest {
	@Test
	void everytingAcummulatedStats() {
		Attributes attributes = getEverything();
		Snapshot snapshot = getSnapshot(attributes);

		AccumulatedSpellStats underTest = snapshot.getStats();

		assertThat(underTest.getBaseStatsIncreasePct()).isEqualTo(10);
		assertThat(underTest.getStaIncreasePct()).isEqualTo(15);
		assertThat(underTest.getSpiIncreasePct()).isEqualTo(-5);
		assertThat(underTest.getStamina()).isEqualTo(462);
		assertThat(underTest.getIntellect()).isEqualTo(413);
		assertThat(underTest.getSpirit()).isEqualTo(20);
		assertThat(underTest.getBaseStatsIncrease()).isEqualTo(20);
		assertThat(underTest.getTotalSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1803);
		assertThat(underTest.getDamageTakenPct()).isEqualTo(31);
		assertThat(underTest.getSpellHitRating()).isEqualTo(164);
		assertThat(underTest.getSpellHitPct()).isEqualTo(3);
		assertThat(underTest.getSpellCritRating()).isEqualTo(331);
		assertThat(underTest.getSpellCritPct()).isEqualTo(11);
		assertThat(underTest.getSpellHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(455);
		assertThat(underTest.getIncreasedCriticalDamagePct()).isEqualTo(3);
		assertThat(underTest.getCritDamageIncreasePct()).isEqualTo(100);
		assertThat(underTest.getExtraCritCoeff()).isEqualTo(0.67, PRECISION);
		assertThat(underTest.getSpellCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTimeReduction()).isEqualTo(0.5);
		assertThat(underTest.getCostReductionPct()).isEqualTo(5);
	}

	@Test
	void everythingSnapshot() {
		Attributes attributes = getEverything();
		Snapshot underTest = getSnapshot(attributes);

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
		Attributes attributes = getEverything();
		Snapshot snapshot = getSnapshot(attributes);
		SpellStatistics underTest = snapshot.getSpellStatistics(CritMode.AVERAGE, true);

		assertThat(underTest.getTotalDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(5219);
		assertThat(underTest.getDps()).usingComparator(ROUNDED_DOWN).isEqualTo(2691);
		assertThat(underTest.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(underTest.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(399);
		assertThat(underTest.getDpm()).usingComparator(ROUNDED_DOWN).isEqualTo(13);
	}

	private Attributes getEverything() {
		return AttributeEvaluator.of()
				.addAttributes(getEquipment())
				.addAttributes(getTalents())
				.addAttributes(getBuffs())
				.addAttributes(Attributes.of(SPELL_DAMAGE, 30)) // +30 sp for 2/2 Demonic Aegis bonus
				.solveAllLeaveAbilities();
	}

	private Snapshot getSnapshot(Attributes stats) {
		Spell spell = getSpell(SHADOW_BOLT);
		Character character = getCharacter(SHADOW_BOLT);
		Enemy enemy = getEnemy();

		return new Snapshot(spell, character, enemy, stats);
	}
}