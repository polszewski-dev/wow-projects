package wow.commons.util;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellRankInfo;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.spells.SpellSchool.FIRE;
import static wow.commons.model.spells.SpellSchool.SHADOW;
import static wow.commons.model.talents.TalentTree.DESTRUCTION;
import static wow.commons.model.unit.CharacterClass.WARLOCK;
import static wow.commons.model.unit.CreatureType.UNDEAD;
import static wow.commons.model.unit.Race.ORC;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class SnapshotTest {
	@BeforeEach
	void setup() {
		spellInfo = new SpellInfo(SHADOW_BOLT, DESTRUCTION, SHADOW, Percent.of(150), Percent.ZERO, null, false, null, false, null, null, null, null, null);
		spellRankInfo = new SpellRankInfo(SHADOW_BOLT, 0, 0, 1000, Duration.seconds(2), false, 0, 0, 0, 0, 0, 0, null, null, null, null);
		baseStatInfo = new BaseStatInfo(0, WARLOCK, ORC, 0, 0, 100, 200, 300, 1000, 2000, Percent.of(10), 100);
		combatRatingInfo = new CombatRatingInfo(0, 10, 20, 40);
	}

	@Test
	@DisplayName("Only base stats")
	void onlyBaseStats() {
		Snapshot snapshot = getSnapshot(Attributes.EMPTY);

		assertThat(snapshot.getStamina()).usingComparator(ROUNDED_DOWN).isEqualTo(100);
		assertThat(snapshot.getIntellect()).usingComparator(ROUNDED_DOWN).isEqualTo(200);
		assertThat(snapshot.getSpirit()).usingComparator(ROUNDED_DOWN).isEqualTo(300);

		assertThat(snapshot.getTotalCrit()).isEqualTo(10, PRECISION);
		assertThat(snapshot.getTotalHit()).isEqualTo(0, PRECISION);
		assertThat(snapshot.getTotalHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getSp()).usingComparator(ROUNDED_DOWN).isEqualTo(0);
		assertThat(snapshot.getSpMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getHitChance()).isEqualTo(0.83, PRECISION);
		assertThat(snapshot.getCritChance()).isEqualTo(0.10, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getHaste()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getDirectDamageDoneMultiplier()).isEqualTo(1, PRECISION);
		assertThat(snapshot.getDotDamageDoneMultiplier()).isEqualTo(1, PRECISION);

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.50, PRECISION);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0, PRECISION);

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(2, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.5, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(2, PRECISION);

		assertThat(snapshot.getManaCost()).usingComparator(ROUNDED_DOWN).isEqualTo(1000);
	}

	@Test
	@DisplayName("Stamina")
	void stamina() {
		Snapshot snapshot = getSnapshot(Attributes.of(STAMINA, 50));

		assertThat(snapshot.getStamina()).isEqualTo(150);
	}

	@Test
	@DisplayName("Intellect")
	void intellect() {
		Snapshot snapshot = getSnapshot(Attributes.of(INTELLECT, 50));

		assertThat(snapshot.getIntellect()).isEqualTo(250);
		assertThat(snapshot.getTotalCrit()).isEqualTo(10.50);
	}

	@Test
	@DisplayName("Spirit")
	void spirit() {
		Snapshot snapshot = getSnapshot(Attributes.of(SPIRIT, 50));

		assertThat(snapshot.getSpirit()).isEqualTo(350);
	}

	@Test
	@DisplayName("Stamina increase %")
	void staminaIncreasePct() {
		Snapshot snapshot = getSnapshot(Attributes.of(STA_INCREASE_PCT, 50));

		assertThat(snapshot.getStamina()).isEqualTo(150);
	}

	@Test
	@DisplayName("Intellect increase %")
	void intellectIncreasePct() {
		Snapshot snapshot = getSnapshot(Attributes.of(INT_INCREASE_PCT, 50));

		assertThat(snapshot.getIntellect()).isEqualTo(300);
		assertThat(snapshot.getTotalCrit()).isEqualTo(11);
	}

	@Test
	@DisplayName("Spirit increase %")
	void spiritIncreasePct() {
		Snapshot snapshot = getSnapshot(Attributes.of(SPI_INCREASE_PCT, 50));

		assertThat(snapshot.getSpirit()).isEqualTo(450);
	}

	@Test
	@DisplayName("Base stats increase %")
	void baseStatsIncreasePct() {
		Snapshot snapshot = getSnapshot(Attributes.of(BASE_STATS_INCREASE_PCT, 50));

		assertThat(snapshot.getStamina()).isEqualTo(150);
		assertThat(snapshot.getIntellect()).isEqualTo(300);
		assertThat(snapshot.getTotalCrit()).isEqualTo(11);
		assertThat(snapshot.getSpirit()).isEqualTo(450);
	}

	@Test
	@DisplayName("Base stats increase")
	void baseStatsIncrease() {
		Snapshot snapshot = getSnapshot(Attributes.of(BASE_STATS_INCREASE, 40));

		assertThat(snapshot.getStamina()).isEqualTo(140);
		assertThat(snapshot.getIntellect()).isEqualTo(240);
		assertThat(snapshot.getTotalCrit()).isEqualTo(10.40);
		assertThat(snapshot.getSpirit()).isEqualTo(340);
	}

	@Test
	@DisplayName("Total crit")
	void totalCrit() {
		Snapshot snapshot = getSnapshot(Attributes.of(
				Attribute.of(SPELL_CRIT_RATING, 40),
				Attribute.of(SPELL_CRIT_PCT, 2)
		));

		assertThat(snapshot.getTotalCrit()).isEqualTo(16);
		assertThat(snapshot.getCritChance()).isEqualTo(0.16, PRECISION);
	}

	@Test
	@DisplayName("Total hit")
	void totalHit() {
		Snapshot snapshot = getSnapshot(Attributes.of(
				Attribute.of(SPELL_HIT_RATING, 40),
				Attribute.of(SPELL_HIT_PCT, 2)
		));

		assertThat(snapshot.getTotalHit()).isEqualTo(4);
		assertThat(snapshot.getHitChance()).isEqualTo(0.04 + 0.83, PRECISION);
	}

	@Test
	@DisplayName("Total haste")
	void totalHaste() {
		Snapshot snapshot = getSnapshot(Attributes.of(
				Attribute.of(SPELL_HASTE_RATING, 40),
				Attribute.of(SPELL_HASTE_PCT, 24)
		));

		assertThat(snapshot.getTotalHaste()).isEqualTo(25);
		assertThat(snapshot.getHaste()).isEqualTo(0.25, PRECISION);
		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(1.6, PRECISION);
		assertThat(snapshot.getEffectiveCastTime().getSeconds()).isEqualTo(1.6, PRECISION);
		assertThat(snapshot.getGcd().getSeconds()).isEqualTo(1.2, PRECISION);
	}

	@Test
	@DisplayName("Total sp")
	void totalSp() {
		Snapshot snapshot = getSnapshot(Attributes.of(
				Attribute.of(SPELL_POWER, 10),
				Attribute.of(SPELL_DAMAGE, 10),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(DESTRUCTION)),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(SHADOW)),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(SHADOW_BOLT)),
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(UNDEAD)),

				Attribute.of(SPELL_DAMAGE, 100, AttributeCondition.of(FIRE)),
				Attribute.of(HEALING_POWER, 100)
		));

		assertThat(snapshot.getSp()).isEqualTo(60);
	}

	@Test
	@DisplayName("Increased crit damage%")
	void critCoeff() {
		Snapshot snapshot = getSnapshot(Attributes.of(INCREASED_CRITICAL_DAMAGE_PCT, 10));

		assertThat(snapshot.getCritCoeff()).isEqualTo(1.65);
	}

	@Test
	@DisplayName("Spell coefficient boonus%")
	void spellCoeff() {
		Snapshot snapshot = getSnapshot(Attributes.of(SPELL_COEFF_BONUS_PCT, 20));

		assertThat(snapshot.getSpellCoeffDirect()).isEqualTo(1.7);
		assertThat(snapshot.getSpellCoeffDoT()).isEqualTo(0.2);
	}

	@Test
	@DisplayName("Cast time reduction")
	void castTime() {
		Snapshot snapshot = getSnapshot(Attributes.of(CAST_TIME_REDUCTION, 0.5));

		assertThat(snapshot.getCastTime().getSeconds()).isEqualTo(1.5);
	}

	@Test
	@DisplayName("Cost reduction reduction%")
	void manaCost() {
		Snapshot snapshot = getSnapshot(Attributes.of(COST_REDUCTION_PCT, 25));

		assertThat(snapshot.getManaCost()).isEqualTo(750);
	}


	/*

ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT:
			case DAMAGE_TAKEN_PCT:
			case EFFECT_INCREASE_PCT:
			case DIRECT_DAMAGE_INCREASE_PCT:
			case DOT_DAMAGE_INCREASE_PCT:
			case EXTRA_CRIT_COEFF:

	 */

	private Snapshot getSnapshot(Attributes stats) {
		return new Snapshot(spellInfo, spellRankInfo, baseStatInfo, combatRatingInfo, stats, activePet, enemyType);
	}

	static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	static final Offset<Double> PRECISION = Offset.offset(0.01);

	SpellInfo spellInfo;
	SpellRankInfo spellRankInfo;
	BaseStatInfo baseStatInfo;
	CombatRatingInfo combatRatingInfo;

	PetType activePet = null;
	CreatureType enemyType = UNDEAD;
}