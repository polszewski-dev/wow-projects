package wow.commons.model.spells;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.ProcEvent;
import wow.commons.util.AttributeEvaluator;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.spells.SpellSchool.FIRE;
import static wow.commons.model.spells.SpellSchool.SHADOW;
import static wow.commons.model.talents.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
class AccumulatedSpellStatsTest extends WowCommonsSpringTest {
	@Test
	void equipmentOnly() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getEquipment())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getStamina()).isEqualTo(462);
		assertThat(underTest.getIntellect()).isEqualTo(373);
		assertThat(underTest.getBaseStatsIncrease()).isEqualTo(6);
		assertThat(underTest.getTotalSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1433);
		assertThat(underTest.getDamageTakenPct()).isEqualTo(6);
		assertThat(underTest.getSpellHitRating()).isEqualTo(164);
		assertThat(underTest.getSpellCritRating()).isEqualTo(317);
		assertThat(underTest.getSpellHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(455);
		assertThat(underTest.getIncreasedCriticalDamagePct()).isEqualTo(3);
	}

	@Test
	void talentsOnly() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getTalents())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getStaIncreasePct()).isEqualTo(15);
		assertThat(underTest.getSpiIncreasePct()).isEqualTo(-5);
		assertThat(underTest.getSpellCritPct()).isEqualTo(8);
		assertThat(underTest.getCritDamageIncreasePct()).isEqualTo(100);
		assertThat(underTest.getExtraCritCoeff()).isEqualTo(0.66, PRECISION);
		assertThat(underTest.getSpellCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTimeReduction()).isEqualTo(0.5);
		assertThat(underTest.getCostReductionPct()).isEqualTo(5);
	}

	@Test
	void buffsOnly() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getBuffs())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getBaseStatsIncreasePct()).isEqualTo(10);
		assertThat(underTest.getIntellect()).isEqualTo(40);
		assertThat(underTest.getSpirit()).isEqualTo(20);
		assertThat(underTest.getBaseStatsIncrease()).isEqualTo(14);
		assertThat(underTest.getTotalSpellDamage()).isEqualTo(340);
		assertThat(underTest.getDamageTakenPct()).isEqualTo(25);
		assertThat(underTest.getSpellHitPct()).isEqualTo(3);
		assertThat(underTest.getSpellCritRating()).isEqualTo(14);
		assertThat(underTest.getSpellCritPct()).isEqualTo(3);
	}

	@Test
	void everything() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getEquipment())
				.addAttributes(getTalents())
				.addAttributes(getBuffs())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getBaseStatsIncreasePct()).isEqualTo(10);
		assertThat(underTest.getStaIncreasePct()).isEqualTo(15);
		assertThat(underTest.getSpiIncreasePct()).isEqualTo(-5);
		assertThat(underTest.getStamina()).isEqualTo(462);
		assertThat(underTest.getIntellect()).isEqualTo(413);
		assertThat(underTest.getSpirit()).isEqualTo(20);
		assertThat(underTest.getBaseStatsIncrease()).isEqualTo(20);
		assertThat(underTest.getTotalSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1773);
		assertThat(underTest.getDamageTakenPct()).isEqualTo(31);
		assertThat(underTest.getSpellHitRating()).isEqualTo(164);
		assertThat(underTest.getSpellHitPct()).isEqualTo(3);
		assertThat(underTest.getSpellCritRating()).isEqualTo(331);
		assertThat(underTest.getSpellCritPct()).isEqualTo(11);
		assertThat(underTest.getSpellHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(455);
		assertThat(underTest.getIncreasedCriticalDamagePct()).isEqualTo(3);
		assertThat(underTest.getCritDamageIncreasePct()).isEqualTo(100);
		assertThat(underTest.getExtraCritCoeff()).isEqualTo(0.66, PRECISION);
		assertThat(underTest.getSpellCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTimeReduction()).isEqualTo(0.5);
		assertThat(underTest.getCostReductionPct()).isEqualTo(5);
	}

	@Test
	@DisplayName("Stamina")
	void stamina() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(STAMINA, 50));

		assertThat(underTest.getStamina()).isEqualTo(50);
	}

	@Test
	@DisplayName("Intellect")
	void intellect() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(INTELLECT, 50));

		assertThat(underTest.getIntellect()).isEqualTo(50);
	}

	@Test
	@DisplayName("Spirit")
	void spirit() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(SPIRIT, 50));

		assertThat(underTest.getSpirit()).isEqualTo(50);
	}

	@Test
	@DisplayName("Stamina increase %")
	void staminaIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(STA_INCREASE_PCT, 50));

		assertThat(underTest.getStaIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Intellect increase %")
	void intellectIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(INT_INCREASE_PCT, 50));

		assertThat(underTest.getIntIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Spirit increase %")
	void spiritIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(SPI_INCREASE_PCT, 50));

		assertThat(underTest.getSpiIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Base stats increase %")
	void baseStatsIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(BASE_STATS_INCREASE_PCT, 50));

		assertThat(underTest.getBaseStatsIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Base stats increase")
	void baseStatsIncrease() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(BASE_STATS_INCREASE, 40));

		assertThat(underTest.getBaseStatsIncrease()).isEqualTo(40);
	}

	@Test
	@DisplayName("Total crit")
	void totalCrit() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_CRIT_RATING, 40),
				Attribute.of(SPELL_CRIT_PCT, 2)
		));

		assertThat(underTest.getSpellCritRating()).isEqualTo(40);
		assertThat(underTest.getSpellCritPct()).isEqualTo(2);
	}

	@Test
	@DisplayName("Total hit")
	void totalHit() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_HIT_RATING, 40),
				Attribute.of(SPELL_HIT_PCT, 2)
		));

		assertThat(underTest.getSpellHitRating()).isEqualTo(40);
		assertThat(underTest.getSpellHitPct()).isEqualTo(2);
	}

	@Test
	@DisplayName("Total haste")
	void totalHaste() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_HASTE_RATING, 40),
				Attribute.of(SPELL_HASTE_PCT, 2)
		));

		assertThat(underTest.getSpellHasteRating()).isEqualTo(40);
		assertThat(underTest.getSpellHastePct()).isEqualTo(2);
	}

	@Test
	@DisplayName("Total sp")
	void totalSp() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_POWER, 10),
				Attribute.of(SPELL_DAMAGE, 10),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(DESTRUCTION)),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(SHADOW)),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(SHADOW_BOLT)),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(UNDEAD)),

				Attribute.of(SPELL_DAMAGE, 100, AttributeCondition.of(FIRE)),
				Attribute.of(HEALING_POWER, 100)
		));

		assertThat(underTest.getTotalSpellDamage()).isEqualTo(60);
	}

	@Test
	@DisplayName("Sp multiplier")
	void spMultiplier() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT, 25));

		assertThat(underTest.getAdditionalSpellDamageTakenPct()).isEqualTo(25);
	}

	@Test
	@DisplayName("Increased crit damage%")
	void critCoeff() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(INCREASED_CRITICAL_DAMAGE_PCT, 10));

		assertThat(underTest.getIncreasedCriticalDamagePct()).isEqualTo(10);
	}

	@Test
	@DisplayName("Crit damage increase%")
	void critDamageIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(CRIT_DAMAGE_INCREASE_PCT, 10));

		assertThat(underTest.getCritDamageIncreasePct()).isEqualTo(10);
	}

	@Test
	@DisplayName("Extra crit coeff%")
	void extraCritCoeffPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(EXTRA_CRIT_COEFF, 10));

		assertThat(underTest.getExtraCritCoeff()).isEqualTo(10);
	}

	@Test
	@DisplayName("Spell coefficient boonus%")
	void spellCoeff() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(SPELL_COEFF_BONUS_PCT, 20));

		assertThat(underTest.getSpellCoeffPct()).isEqualTo(20);
	}

	@Test
	@DisplayName("Damage taken%")
	void damageTakenPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DAMAGE_TAKEN_PCT, 50));

		assertThat(underTest.getDamageTakenPct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Effect increase%")
	void effectIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(EFFECT_INCREASE_PCT, 50));

		assertThat(underTest.getEffectIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Direct damage multiplier%")
	void directDamageMultiplierPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DIRECT_DAMAGE_INCREASE_PCT, 50));

		assertThat(underTest.getDirectDamageIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("DoT damage multiplier%")
	void dotDamageMultiplierPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DOT_DAMAGE_INCREASE_PCT, 50));

		assertThat(underTest.getDotDamageIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Cast time reduction")
	void castTime() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(CAST_TIME_REDUCTION, 0.5));

		assertThat(underTest.getCastTimeReduction()).isEqualTo(0.5);
	}

	@Test
	@DisplayName("Cost reduction reduction%")
	void manaCost() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(COST_REDUCTION_PCT, 25));

		assertThat(underTest.getCostReductionPct()).isEqualTo(25);
	}

	@Test
	@DisplayName("Solving special ability with internal cooldown")
	void specialAbility() {
		SpecialAbility proc = SpecialAbility.proc(
				ProcEvent.SPELL_CRIT,
				Percent._100,
				Attributes.of(SPELL_DAMAGE, 100),
				Duration.seconds(15),
				Duration.seconds(60),
				"test"
		);

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(proc));

		assertThat(underTest.getTotalSpellDamage()).isEqualTo(25);
	}

	private AccumulatedSpellStats getAccumulatedSpellStats(Attributes attributes) {
		Spell spell = getSpell(SHADOW_BOLT);

		StatProvider dummyStatProvider = StatProvider.fixedValues(0.99, 0.30, spell.getCastTime());

		AccumulatedSpellStats stats = new AccumulatedSpellStats(attributes, spell.getConditions(null, UNDEAD));

		stats.accumulateStats(dummyStatProvider);

		return stats;
	}
}