package wow.estimator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.config.Described;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.SpellId;
import wow.estimator.model.EffectList;
import wow.estimator.model.SpecialAbility;
import wow.test.commons.TalentNames;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.buff.BuffCategory.SELF_BUFF;
import static wow.commons.model.spell.SpellSchool.FIRE;
import static wow.commons.model.spell.SpellSchool.SHADOW;
import static wow.estimator.constant.AbilityIds.CURSE_OF_DOOM;
import static wow.estimator.constant.AbilityIds.SHADOW_BOLT;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class CalculationServiceTest extends ServiceTest {
	@Autowired
	CalculationService calculationService;

	@Test
	void getTalentSpEquivalent() {
		double spEquivalent = calculationService.getTalentSpEquivalent(TalentNames.RUIN, player);

		assertThat(spEquivalent).isEqualTo(288.45, PRECISION);
	}

	@Test
	void getRotationDps() {
		var effectList = EffectList.createSolved(player);
		var targetEffectList = EffectList.createSolvedForTarget(player);
		var dps = calculationService.getRotationDps(player, player.getRotation(), effectList, targetEffectList);

		assertThat(dps).isEqualTo(2799.67, PRECISION);
	}

	@Test
	void getAccumulatedRotationStats() {
		var stats = calculationService.getAccumulatedRotationStats(player, player.getRotation());

		var baseStats = stats.getBaseStats();

		assertThat(baseStats.getIntellect()).isEqualTo(555);
		assertThat(baseStats.getIntellectPct()).isZero();
		assertThat(baseStats.getBaseStats()).isEqualTo(20);
		assertThat(baseStats.getBaseStatsPct()).isEqualTo(10);

		var abilityStats = stats.get(SHADOW_BOLT);

		assertThat(abilityStats.getCast().getCastTime()).isEqualTo(-0.5);
		assertThat(abilityStats.getCast().getCastTimePct()).isZero();
		assertThat(abilityStats.getCast().getHasteRating()).isEqualTo(504);
		assertThat(abilityStats.getCast().getHastePct()).isZero();

		var costStats = abilityStats.getCost();

		assertThat(costStats.getManaCostPct()).isEqualTo(-5);

		var targetStats = abilityStats.getTarget();

		assertThat(targetStats.getAmountTakenPct()).isEqualTo(10);

		var hitStats = abilityStats.getHit();

		assertThat(hitStats.getHitRating()).isEqualTo(169);
		assertThat(hitStats.getHitPct()).isEqualTo(3);

		assertThat(stats.getStatConversions()).isEmpty();
		assertThat(stats.getNonModifierEffects().stream().map(Described::getName).toList()).isEqualTo(List.of(
				"Fel Domination",
				"Demonic Sacrifice",
				"Improved Shadow Bolt",
				"Shadowburn",
				"Nether Protection",
				"Soul Leech",
				"Underwater Breathing",
				"Mana Spring Totem",
				"Malefic Raiment - P2 bonus"
		));
		assertThat(stats.getActivatedAbilities().stream().map(Described::getName).toList()).isEqualTo(List.of(
				"Shifting Naaru Sliver",
				"The Skull of Gul'dan"
		));
	}

	@Test
	void getAccumulatedRotationStatsDirectComponentOnly() {
		var stats = calculationService.getAccumulatedRotationStats(player, player.getRotation());
		var abilityStats = stats.get(SHADOW_BOLT);

		var direct = abilityStats.getDirect();

		assertThat(direct.getAmount()).isZero();
		assertThat(direct.getAmountPct()).isEqualTo(21);
		assertThat(direct.getEffectPct()).isZero();
		assertThat(direct.getPower()).isEqualTo(1738);
		assertThat(direct.getPowerPct()).isZero();
		assertThat(direct.getPowerCoeffPct()).isEqualTo(20);
		assertThat(direct.getCritRating()).isEqualTo(270);
		assertThat(direct.getCritPct()).isEqualTo(12.7, PRECISION);
		assertThat(direct.getCritEffectPct()).isEqualTo(3);
		assertThat(direct.getCritEffectMultiplierPct()).isEqualTo(100);
		assertThat(direct.getCritCoeffPct()).isZero();

		assertThat(abilityStats.getPeriodic()).isNull();
		assertThat(abilityStats.getEffectDuration()).isNull();
	}

	@Test
	void getRotationStats() {
		var stats = calculationService.getRotationStats(player, player.getRotation());

		assertThat(stats.getDps()).isEqualTo(2799.67, PRECISION);
		assertThat(stats.getTotalDamage()).isEqualTo(839903.18, PRECISION);
		assertThat(stats.getStatList()).hasSize(2);

		var codStats = stats.getStatList().get(0);
		var sbStats = stats.getStatList().get(1);

		assertThat(codStats.getSpell().getAbilityId()).isEqualTo(CURSE_OF_DOOM);
		assertThat(codStats.getDamage()).isEqualTo(9630.22, PRECISION);
		assertThat(codStats.getNumCasts()).isEqualTo(5);

		assertThat(sbStats.getSpell().getAbilityId()).isEqualTo(SHADOW_BOLT);
		assertThat(sbStats.getDamage()).isEqualTo(5024.67, PRECISION);
		assertThat(sbStats.getNumCasts()).isEqualTo(157.57, PRECISION);
	}

	@Test
	void getAbilityStats() {
		var ability = getAbility(SHADOW_BOLT);
		var stats = calculationService.getAbilityStats(player, ability, true, 10);

		assertThat(stats.getTotalDamage()).isEqualTo(5024.67, PRECISION);
		assertThat(stats.getDps()).isEqualTo(2689.42, PRECISION);
		assertThat(stats.getCastTime().getSeconds()).isEqualTo(1.86, PRECISION);
		assertThat(stats.getManaCost()).isEqualTo(399);
		assertThat(stats.getDpm()).isEqualTo(12.59, PRECISION);
		assertThat(stats.getHitSpEqv()).isEqualTo(0, PRECISION);
		assertThat(stats.getCritSpEqv()).isEqualTo(10.88, PRECISION);
		assertThat(stats.getHasteSpEqv()).isEqualTo(11.44, PRECISION);
	}

	@Test
	void getCurrentStats() {
		var stats = calculationService.getCurrentStats(player);

		assertThat(stats.getSpellDamage()).isEqualTo(1604);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1738);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1684);
		assertThat(stats.getSpellHitPctBonus()).isEqualTo(16.39, PRECISION);
		assertThat(stats.getSpellHitPct()).isEqualTo(99.00, PRECISION);
		assertThat(stats.getSpellCritPct()).isEqualTo(27.82, PRECISION);
		assertThat(stats.getSpellHastePct()).isEqualTo(31.96, PRECISION);
		assertThat(stats.getSpellHitRating()).isEqualTo(169);
		assertThat(stats.getSpellCritRating()).isEqualTo(270);
		assertThat(stats.getSpellHasteRating()).isEqualTo(504);
		assertThat(stats.getStamina()).isEqualTo(818);
		assertThat(stats.getIntellect()).isEqualTo(632);
		assertThat(stats.getSpirit()).isEqualTo(245);
		assertThat(stats.getMaxHealth()).isEqualTo(11834);
		assertThat(stats.getMaxMana()).isEqualTo(12155);
	}

	@Disabled
	@Test
	void getStats() {
		var stats = calculationService.getStats(player, SELF_BUFF);

		assertThat(stats.getSpellDamage()).isEqualTo(1444);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1498);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1444);
		assertThat(stats.getSpellHitPctBonus()).isEqualTo(13.39, PRECISION);
		assertThat(stats.getSpellHitPct()).isEqualTo(96.39, PRECISION);
		assertThat(stats.getSpellCritPct()).isEqualTo(22.80, PRECISION);
		assertThat(stats.getSpellHastePct()).isEqualTo(31.96, PRECISION);
		assertThat(stats.getSpellHitRating()).isEqualTo(169);
		assertThat(stats.getSpellCritRating()).isEqualTo(256);
		assertThat(stats.getSpellHasteRating()).isEqualTo(504);
		assertThat(stats.getStamina()).isEqualTo(646);
		assertThat(stats.getIntellect()).isEqualTo(521);
		assertThat(stats.getSpirit()).isEqualTo(142);
		assertThat(stats.getMaxHealth()).isEqualTo(10063);
		assertThat(stats.getMaxMana()).isEqualTo(10440);
	}

	@Test
	void getEquipmentStats() {
		var stats = calculationService.getEquipmentStats(player);

		assertThat(stats.getSpellDamage()).isEqualTo(1314);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1368);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1314);
		assertThat(stats.getSpellHitPctBonus()).isEqualTo(13.39, PRECISION);
		assertThat(stats.getSpellHitPct()).isEqualTo(13.39, PRECISION);
		assertThat(stats.getSpellCritPct()).isEqualTo(16.47, PRECISION);
		assertThat(stats.getSpellHastePct()).isEqualTo(31.96, PRECISION);
		assertThat(stats.getSpellHitRating()).isEqualTo(169);
		assertThat(stats.getSpellCritRating()).isEqualTo(256);
		assertThat(stats.getSpellHasteRating()).isEqualTo(504);
		assertThat(stats.getStamina()).isEqualTo(485);
		assertThat(stats.getIntellect()).isEqualTo(390);
		assertThat(stats.getSpirit()).isEqualTo(6);
		assertThat(stats.getMaxHealth()).isEqualTo(4850);
		assertThat(stats.getMaxMana()).isEqualTo(5850);
	}

	@Test
	void getSpecialAbilityStats() {
		var activatedAbility = (ActivatedAbility) spellRepository.getSpell(SpellId.of(132483), PHASE).orElseThrow();//The Skull of Gul'dan
		var stats = calculationService.getSpecialAbilityStats(SpecialAbility.of(activatedAbility), player);

		assertThat(stats.getSpEquivalent()).isEqualTo(32.31, PRECISION);
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		equipGearSet(player);
	}
}