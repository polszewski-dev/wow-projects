package wow.character.model.snapshot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;

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
class AccumulatedSpellStatsTest extends WowCharacterSpringTest {
	@Test
	void equipmentOnly() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getEquipment())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getStamina()).isEqualTo(462);
		assertThat(underTest.getIntellect()).isEqualTo(373);
		assertThat(underTest.getBaseStats()).isEqualTo(6);
		assertThat(underTest.getSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1380);
		assertThat(underTest.getDamagePct()).isZero();
		assertThat(underTest.getHitRating()).isEqualTo(164);
		assertThat(underTest.getCritRating()).isEqualTo(317);
		assertThat(underTest.getHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(426);
		assertThat(underTest.getCritDamagePct()).isEqualTo(3);
	}

	@Test
	void talentsOnly() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getTalents())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getStaminaPct()).isEqualTo(15);
		assertThat(underTest.getSpiritPct()).isEqualTo(-5);
		assertThat(underTest.getCritPct()).isEqualTo(8);
		assertThat(underTest.getCritDamageMultiplierPct()).isEqualTo(100);
		assertThat(underTest.getCritCoeffPct()).isZero();
		assertThat(underTest.getSpellPowerCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTime()).isEqualTo(-0.5);
		assertThat(underTest.getCostPct()).isEqualTo(-5);
		assertThat(underTest.getMaxHealthPct()).isEqualTo(3);
		assertThat(underTest.getMaxManaPct()).isEqualTo(3);
	}

	@Test
	void buffsOnly() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getBuffs())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getBaseStatsPct()).isEqualTo(10);
		assertThat(underTest.getIntellect()).isEqualTo(40);
		assertThat(underTest.getSpirit()).isEqualTo(20);
		assertThat(underTest.getBaseStats()).isEqualTo(14);
		assertThat(underTest.getSpellDamage()).isEqualTo(340);
		assertThat(underTest.getDamagePct()).isEqualTo(25);
		assertThat(underTest.getHitPct()).isEqualTo(3);
		assertThat(underTest.getCritRating()).isEqualTo(14);
		assertThat(underTest.getCritPct()).isEqualTo(3);
	}

	@Test
	void everything() {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(getEquipment())
				.addAttributes(getTalents())
				.addAttributes(getBuffs())
				.solveAllLeaveAbilities();

		AccumulatedSpellStats underTest = getAccumulatedSpellStats(attributes);

		assertThat(underTest.getBaseStatsPct()).isEqualTo(10);
		assertThat(underTest.getStaminaPct()).isEqualTo(15);
		assertThat(underTest.getSpiritPct()).isEqualTo(-5);
		assertThat(underTest.getStamina()).isEqualTo(462);
		assertThat(underTest.getIntellect()).isEqualTo(413);
		assertThat(underTest.getSpirit()).isEqualTo(20);
		assertThat(underTest.getBaseStats()).isEqualTo(20);
		assertThat(underTest.getSpellDamage()).usingComparator(ROUNDED_DOWN).isEqualTo(1720);
		assertThat(underTest.getDamagePct()).isEqualTo(25);
		assertThat(underTest.getHitRating()).isEqualTo(164);
		assertThat(underTest.getHitPct()).isEqualTo(3);
		assertThat(underTest.getCritRating()).isEqualTo(331);
		assertThat(underTest.getCritPct()).isEqualTo(11);
		assertThat(underTest.getHasteRating()).usingComparator(ROUNDED_DOWN).isEqualTo(426);
		assertThat(underTest.getCritDamagePct()).isEqualTo(3);
		assertThat(underTest.getCritDamageMultiplierPct()).isEqualTo(100);
		assertThat(underTest.getCritCoeffPct()).isZero();
		assertThat(underTest.getSpellPowerCoeffPct()).isEqualTo(20);
		assertThat(underTest.getCastTime()).isEqualTo(-0.5);
		assertThat(underTest.getCostPct()).isEqualTo(-5);
		assertThat(underTest.getMaxHealthPct()).isEqualTo(3);
		assertThat(underTest.getMaxManaPct()).isEqualTo(3);
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
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(STAMINA_PCT, 50));

		assertThat(underTest.getStaminaPct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Intellect increase %")
	void intellectIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(INTELLECT_PCT, 50));

		assertThat(underTest.getIntellectPct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Spirit increase %")
	void spiritIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(SPIRIT_PCT, 50));

		assertThat(underTest.getSpiritPct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Base stats increase %")
	void baseStatsIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(BASE_STATS_PCT, 50));

		assertThat(underTest.getBaseStatsPct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Base stats increase")
	void baseStatsIncrease() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(BASE_STATS, 40));

		assertThat(underTest.getBaseStats()).isEqualTo(40);
	}

	@Test
	@DisplayName("Total crit")
	void totalCrit() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_CRIT_RATING, 40),
				Attribute.of(SPELL_CRIT_PCT, 2)
		));

		assertThat(underTest.getCritRating()).isEqualTo(40);
		assertThat(underTest.getCritPct()).isEqualTo(2);
	}

	@Test
	@DisplayName("Total hit")
	void totalHit() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_HIT_RATING, 40),
				Attribute.of(SPELL_HIT_PCT, 2)
		));

		assertThat(underTest.getHitRating()).isEqualTo(40);
		assertThat(underTest.getHitPct()).isEqualTo(2);
	}

	@Test
	@DisplayName("Total haste")
	void totalHaste() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(
				Attribute.of(SPELL_HASTE_RATING, 40),
				Attribute.of(SPELL_HASTE_PCT, 2)
		));

		assertThat(underTest.getHasteRating()).isEqualTo(40);
		assertThat(underTest.getHastePct()).isEqualTo(2);
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

		assertThat(underTest.getSpellDamage()).isEqualTo(60);
	}

	@Test
	@DisplayName("Sp multiplier")
	void spMultiplier() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(SPELL_DAMAGE_PCT, 25));

		assertThat(underTest.getSpellDamagePct()).isEqualTo(25);
	}

	@Test
	@DisplayName("Increased crit damage%")
	void critCoeff() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(CRIT_DAMAGE_PCT, 10));

		assertThat(underTest.getCritDamagePct()).isEqualTo(10);
	}

	@Test
	@DisplayName("Crit damage increase%")
	void critDamageIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(CRIT_DAMAGE_MULTIPLIER_PCT, 10));

		assertThat(underTest.getCritDamageMultiplierPct()).isEqualTo(10);
	}

	@Test
	@DisplayName("Extra crit coeff%")
	void extraCritCoeffPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(CRIT_COEFF_PCT, 10));

		assertThat(underTest.getCritCoeffPct()).isEqualTo(10);
	}

	@Test
	@DisplayName("Spell coefficient boonus%")
	void spellCoeff() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(POWER_COEFFICIENT_PCT, 20));

		assertThat(underTest.getSpellPowerCoeffPct()).isEqualTo(20);
	}

	@Test
	@DisplayName("Damage taken%")
	void damageTakenPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DAMAGE_PCT, 50));

		assertThat(underTest.getDamagePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Effect increase%")
	void effectIncreasePct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(EFFECT_PCT, 50));

		assertThat(underTest.getEffectIncreasePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Direct damage multiplier%")
	void directDamageMultiplierPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DIRECT_DAMAGE_PCT, 50));

		assertThat(underTest.getDirectDamagePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("DoT damage multiplier%")
	void dotDamageMultiplierPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DOT_DAMAGE_PCT, 50));

		assertThat(underTest.getDotDamagePct()).isEqualTo(50);
	}

	@Test
	@DisplayName("Cast time reduction")
	void castTime() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(CAST_TIME, -0.5));

		assertThat(underTest.getCastTime()).isEqualTo(-0.5);
	}

	@Test
	@DisplayName("Cost reduction reduction%")
	void manaCost() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(COST_PCT, -25));

		assertThat(underTest.getCostPct()).isEqualTo(-25);
	}

	@Test
	@DisplayName("Duration")
	void duration() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DURATION, 5));

		assertThat(underTest.getDuration()).isEqualTo(5);
	}

	@Test
	@DisplayName("Duration%")
	void durationPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(DURATION_PCT, 5));

		assertThat(underTest.getDurationPct()).isEqualTo(5);
	}

	@Test
	@DisplayName("Cooldown")
	void cooldown() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(COOLDOWN, 5));

		assertThat(underTest.getCooldown()).isEqualTo(5);
	}

	@Test
	@DisplayName("Cooldown%")
	void cooldownPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(COOLDOWN_PCT, 5));

		assertThat(underTest.getCooldownPct()).isEqualTo(5);
	}

	@Test
	@DisplayName("Threat%")
	void threatPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(THREAT_PCT, 5));

		assertThat(underTest.getThreatPct()).isEqualTo(5);
	}

	@Test
	@DisplayName("Pushback%")
	void pushbackPct() {
		AccumulatedSpellStats underTest = getAccumulatedSpellStats(Attributes.of(PUSHBACK_PCT, 5));

		assertThat(underTest.getPushbackPct()).isEqualTo(5);
	}

	private AccumulatedSpellStats getAccumulatedSpellStats(Attributes attributes) {
		Character character = getCharacter();
		Spell spell = getSpell(SHADOW_BOLT);

		AccumulatedSpellStats stats = new AccumulatedSpellStats(attributes, character.getConditions(spell));

		stats.accumulatePrimitiveAttributes();

		return stats;
	}
}