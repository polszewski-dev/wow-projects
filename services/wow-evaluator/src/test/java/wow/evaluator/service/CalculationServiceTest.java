package wow.evaluator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.config.Described;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.talent.TalentId;
import wow.evaluator.model.EffectList;
import wow.evaluator.model.SpecialAbility;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.buff.BuffCategory.SELF_BUFF;
import static wow.commons.model.spell.AbilityId.CURSE_OF_DOOM;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.SpellSchool.FIRE;
import static wow.commons.model.spell.SpellSchool.SHADOW;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class CalculationServiceTest extends ServiceTest {
	@Autowired
	CalculationService calculationService;

	@Test
	void getSpEquivalent() {
		double spEquivalent = calculationService.getSpEquivalent(TalentId.RUIN, character);

		assertThat(spEquivalent).isEqualTo(306.89, PRECISION);
	}

	@Test
	void getRotationDps() {
		var effectList = EffectList.createSolved(character);
		var targetEffectList = EffectList.createSolvedForTarget(character);
		var dps = calculationService.getRotationDps(character, character.getRotation(), effectList, targetEffectList);

		assertThat(dps).isEqualTo(2779.80, PRECISION);
	}

	@Test
	void getAccumulatedRotationStats() {
		var stats = calculationService.getAccumulatedRotationStats(character, character.getRotation());

		var baseStats = stats.getBaseStats();

		assertThat(baseStats.getIntellect()).isEqualTo(544);
		assertThat(baseStats.getIntellectPct()).isZero();
		assertThat(baseStats.getBaseStats()).isEqualTo(20);
		assertThat(baseStats.getBaseStatsPct()).isEqualTo(10);

		var abilityStats = stats.get(SHADOW_BOLT);

		assertThat(abilityStats.getCast().getCastTime()).isEqualTo(-0.5);
		assertThat(abilityStats.getCast().getCastTimePct()).isZero();
		assertThat(abilityStats.getCast().getHasteRating()).isEqualTo(426);
		assertThat(abilityStats.getCast().getHastePct()).isZero();

		var costStats = abilityStats.getCost();

		assertThat(costStats.getManaCostPct()).isEqualTo(-5);

		var targetStats = abilityStats.getTarget();

		assertThat(targetStats.getDamageTakenPct()).isEqualTo(10);

		var hitStats = abilityStats.getHit();

		assertThat(hitStats.getHitRating()).isEqualTo(164);
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
				"Malefic Raiment - P2 bonus"
		));
		assertThat(stats.getActivatedAbilities().stream().map(Described::getName).toList()).isEqualTo(List.of(
				"The Skull of Gul'dan",
				"Shifting Naaru Sliver"
		));
	}

	@Test
	void getAccumulatedRotationStatsDirectComponentOnly() {
		var stats = calculationService.getAccumulatedRotationStats(character, character.getRotation());
		var abilityStats = stats.get(SHADOW_BOLT);

		var direct = abilityStats.getDirect();

		assertThat(direct.getDamage()).isZero();
		assertThat(direct.getDamagePct()).isEqualTo(21);
		assertThat(direct.getEffectPct()).isZero();
		assertThat(direct.getPower()).isEqualTo(1750);
		assertThat(direct.getPowerPct()).isZero();
		assertThat(direct.getPowerCoeffPct()).isEqualTo(20);
		assertThat(direct.getCritRating()).isEqualTo(331);
		assertThat(direct.getCritPct()).isEqualTo(12.7, PRECISION);
		assertThat(direct.getCritDamagePct()).isEqualTo(3);
		assertThat(direct.getCritDamageMultiplierPct()).isEqualTo(100);
		assertThat(direct.getCritCoeffPct()).isZero();

		assertThat(abilityStats.getPeriodic()).isNull();
		assertThat(abilityStats.getEffectDuration()).isNull();
	}

	@Test
	void getRotationStats() {
		var stats = calculationService.getRotationStats(character, character.getRotation());

		assertThat(stats.getDps()).isEqualTo(2779.80, PRECISION);
		assertThat(stats.getTotalDamage()).isEqualTo(833940.11, PRECISION);
		assertThat(stats.getStatList()).hasSize(2);

		var codStats = stats.getStatList().get(0);
		var sbStats = stats.getStatList().get(1);

		assertThat(codStats.getSpell().getAbilityId()).isEqualTo(CURSE_OF_DOOM);
		assertThat(codStats.getDamage()).isEqualTo(9659.92, PRECISION);
		assertThat(codStats.getNumCasts()).isEqualTo(5);

		assertThat(sbStats.getSpell().getAbilityId()).isEqualTo(SHADOW_BOLT);
		assertThat(sbStats.getDamage()).isEqualTo(5181.05, PRECISION);
		assertThat(sbStats.getNumCasts()).isEqualTo(151.63, PRECISION);
	}

	@Test
	void getSpellStats() {
		var ability = getAbility(SHADOW_BOLT);
		var stats = calculationService.getSpellStats(character, ability, true, 10);

		assertThat(stats.getTotalDamage()).isEqualTo(5181.05, PRECISION);
		assertThat(stats.getDps()).isEqualTo(2670.61, PRECISION);
		assertThat(stats.getCastTime().getSeconds()).isEqualTo(1.94, PRECISION);
		assertThat(stats.getManaCost()).isEqualTo(399);
		assertThat(stats.getDpm()).isEqualTo(12.98, PRECISION);
		assertThat(stats.getHitSpEqv()).isEqualTo(0, PRECISION);
		assertThat(stats.getCritSpEqv()).isEqualTo(10.30, PRECISION);
		assertThat(stats.getHasteSpEqv()).isEqualTo(11.44, PRECISION);
	}

	@Test
	void getCurrentStats() {
		var stats = calculationService.getCurrentStats(character);

		assertThat(stats.getSpellDamage()).isEqualTo(1616);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1750);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1696);
		assertThat(stats.getSpellHitPctBonus()).isEqualTo(16.00, PRECISION);
		assertThat(stats.getSpellHitPct()).isEqualTo(99.00, PRECISION);
		assertThat(stats.getSpellCritPct()).isEqualTo(30.44, PRECISION);
		assertThat(stats.getSpellHastePct()).isEqualTo(27.01, PRECISION);
		assertThat(stats.getSpellHitRating()).isEqualTo(164);
		assertThat(stats.getSpellCritRating()).isEqualTo(331);
		assertThat(stats.getSpellHasteRating()).isEqualTo(426);
		assertThat(stats.getStamina()).isEqualTo(797);
		assertThat(stats.getIntellect()).isEqualTo(620);
		assertThat(stats.getSpirit()).isEqualTo(245);
		assertThat(stats.getMaxHealth()).isEqualTo(11618);
		assertThat(stats.getMaxMana()).isEqualTo(11969);
	}

	@Test
	void getStats() {
		var stats = calculationService.getStats(character, SELF_BUFF);

		assertThat(stats.getSpellDamage()).isEqualTo(1456);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1510);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1456);
		assertThat(stats.getSpellHitPctBonus()).isEqualTo(12.99, PRECISION);
		assertThat(stats.getSpellHitPct()).isEqualTo(95.99, PRECISION);
		assertThat(stats.getSpellCritPct()).isEqualTo(25.43, PRECISION);
		assertThat(stats.getSpellHastePct()).isEqualTo(27.01, PRECISION);
		assertThat(stats.getSpellHitRating()).isEqualTo(164);
		assertThat(stats.getSpellCritRating()).isEqualTo(317);
		assertThat(stats.getSpellHasteRating()).isEqualTo(426);
		assertThat(stats.getStamina()).isEqualTo(626);
		assertThat(stats.getIntellect()).isEqualTo(510);
		assertThat(stats.getSpirit()).isEqualTo(142);
		assertThat(stats.getMaxHealth()).isEqualTo(9857);
		assertThat(stats.getMaxMana()).isEqualTo(10270);
	}

	@Test
	void getEquipmentStats() {
		var stats = calculationService.getEquipmentStats(character);

		assertThat(stats.getSpellDamage()).isEqualTo(1326);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(SHADOW, 1380);
		assertThat(stats.getSpellDamageBySchool()).containsEntry(FIRE, 1326);
		assertThat(stats.getSpellHitPctBonus()).isEqualTo(13.00, PRECISION);
		assertThat(stats.getSpellHitPct()).isEqualTo(13.00, PRECISION);
		assertThat(stats.getSpellCritPct()).isEqualTo(19.09, PRECISION);
		assertThat(stats.getSpellHastePct()).isEqualTo(27.01, PRECISION);
		assertThat(stats.getSpellHitRating()).isEqualTo(164);
		assertThat(stats.getSpellCritRating()).isEqualTo(317);
		assertThat(stats.getSpellHasteRating()).isEqualTo(426);
		assertThat(stats.getStamina()).isEqualTo(468);
		assertThat(stats.getIntellect()).isEqualTo(379);
		assertThat(stats.getSpirit()).isEqualTo(6);
		assertThat(stats.getMaxHealth()).isEqualTo(4680);
		assertThat(stats.getMaxMana()).isEqualTo(5685);
	}

	@Test
	void getSpecialAbilityStats() {
		var activatedAbility = (ActivatedAbility) spellRepository.getSpell(132483, PHASE).orElseThrow();//The Skull of Gul'dan
		var stats = calculationService.getSpecialAbilityStats(SpecialAbility.of(activatedAbility), character);

		assertThat(stats.getSpEquivalent()).isEqualTo(33.30, PRECISION);
	}

	@BeforeEach
	@Override
	void setup() {
		super.setup();

		character.setEquipment(getEquipment());
	}
}