package wow.simulator.simulation.spell.tbc.talent;

import wow.character.model.snapshot.StatSummary;
import wow.commons.model.Duration;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
import wow.simulator.simulation.spell.SpellSimulationTest;

import java.util.Set;
import java.util.function.ToIntFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.simulator.util.CalcUtils.increaseByPct;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
public abstract class TalentSimulationTest extends SpellSimulationTest {
	private String simulatedAbilityName;
	private int unmodifiedRollIdx = 1;

	protected void simulateTalent(String talentName, int rank, String abilityName) {
		this.simulatedAbilityName = abilityName;

		addSpBonus(player, 100);
		addSpBonus(player2, 100);

		enableTalent(talentName, rank);

		if (isHealingAbility(abilityName)) {
			testingHealingAbility();
		}

		if (isAoEgAbility(abilityName)) {
			testingAoeAbility();
		}

		enableRequiredTalent(abilityName);

		player.cast(abilityName);
		player2.cast(abilityName);

		updateUntil(60);
	}

	protected void assertDamageIsIncreasedByPct(double pctIncrease) {
		var baseDamage = getBaseDamage();
		var modifiedDamage = getModifiedDamage();

		assertIsIncreasedByPctNonExact(modifiedDamage, baseDamage, pctIncrease);
	}

	protected void assertDirectDamageIsIncreasedByPct(double pctIncrease) {
		var baseDamage = getBaseDirectDamage();
		var modifiedDamage = getModifiedDirectDamage();

		assertIsIncreasedByPctNonExact(modifiedDamage, baseDamage, pctIncrease);
	}

	protected void assertHealingIsIncreasedByPct(double pctIncrease) {
		var baseHealing = getBaseHealing();
		var modifiedHealing = getModifiedHealing();

		assertIsIncreasedByPctNonExact(modifiedHealing, baseHealing, pctIncrease);
	}

	protected void assertSelfHealingIsIncreasedByPct(double pctIncrease) {
		var baseHealing = getBaseSelfHealing();
		var modifiedHealing = getModifiedSelfHealing();

		assertIsIncreasedByPctNonExact(modifiedHealing, baseHealing, pctIncrease);
	}

	protected void assertHitChanceIsIncreasedByPct(int increase) {
		var baseHitChance = getBaseHitChance();
		var modifiedHitChance = getModifiedHitChance();

		assertIsIncreasedBy(modifiedHitChance, baseHitChance, increase);
	}

	protected void assertCritChanceIsIncreasedByPct(int increase) {
		var baseCritChance = getBaseCritChance();
		var modifiedCritChance = getModifiedCritChance();

		assertIsIncreasedBy(modifiedCritChance, baseCritChance, increase);
	}

	protected void assertDamageCoefficientIsIncreasedBy(int increase) {
		var baseDamage = getBaseDamage();
		var modifiedDamage = getModifiedDamage();
		var damageIncrease = getPercentOf(increase, player.getStats().getSpellDamage());

		assertIsIncreasedBy(modifiedDamage, baseDamage, damageIncrease);
	}

	protected void assertHealingCoefficientIsIncreasedBy(int increase) {
		var baseHealing = getBaseHealing();
		var modifiedHealing = getModifiedHealing();
		var healingIncrease = getPercentOf(increase, player.getStats().getSpellHealing());

		assertIsIncreasedBy(modifiedHealing, baseHealing, healingIncrease);
	}

	protected void assertCritDamageBonusIsIncreasedByPct(int pctIncrease) {
		var critDamageBonus = increaseByPct(50.0, pctIncrease);

		assertDirectDamageIsIncreasedByPct(critDamageBonus);
	}

	protected void assertCastTimeIsReducedBy(double reduction) {
		var baseCastTime = getBaseCastTime();
		var modifiedCastTime = getModifiedCastTime();

		assertIsIncreasedBy(modifiedCastTime, baseCastTime, -reduction);
	}

	protected void assertCastTimeIsReducedByPct(int pctReduction) {
		var baseCastTime = getBaseCastTime();
		var modifiedCastTime = getModifiedCastTime();

		assertIsIncreasedByPct(modifiedCastTime, baseCastTime, -pctReduction);
	}

	protected void assertManaCostIsIncreasedByPct(int pctIncrease) {
		var baseManaCost = getBaseManaCost();
		var modifiedManaCost = getModifiedManaCost();

		assertIsIncreasedByPct(modifiedManaCost, baseManaCost, pctIncrease);
	}

	protected void assertManaCostIsReducedByPct(int pctReduction) {
		var baseManaCost = getBaseManaCost();
		var modifiedManaCost = getModifiedManaCost();

		assertIsIncreasedByPct(modifiedManaCost, baseManaCost, -pctReduction);
	}

	protected void assertCooldownIsReducedBy(double reduction) {
		var baseCooldown = getBaseCooldown();
		var modifiedCooldown = getModifiedCooldown();

		assertIsIncreasedBy(modifiedCooldown, baseCooldown, -reduction);
	}

	protected void assertCooldownIsReducedByPct(int pctReduction) {
		var baseCooldown = getBaseCooldown();
		var modifiedCooldown = getModifiedCooldown();

		assertIsIncreasedByPct(modifiedCooldown, baseCooldown, -pctReduction);
	}

	protected void assertEffectDurationIsIncreasedBy(int increase) {
		var baseDuration = getBaseDuration();
		var modifiedDuration = getModifiedDuration();

		assertIsIncreasedBy(modifiedDuration, baseDuration, increase);
	}

	private Double getBaseHitChance() {
		return getHitChanceNo(unmodifiedRollIdx);
	}

	private Double getModifiedHitChance() {
		return getHitChanceNo(0);
	}

	private Double getBaseCritChance() {
		return getCritChanceNo(unmodifiedRollIdx);
	}

	private Double getModifiedCritChance() {
		return getCritChanceNo(0);
	}

	private int getBaseDamage() {
		return handler.getDamageDone(simulatedAbilityName, player2.getTarget(), player2);
	}

	private int getModifiedDamage() {
		return handler.getDamageDone(simulatedAbilityName, player.getTarget(), player);
	}

	private int getBaseDirectDamage() {
		return handler.getDamageDone(0, simulatedAbilityName, player2.getTarget(), player2);
	}

	private int getModifiedDirectDamage() {
		return handler.getDamageDone(0, simulatedAbilityName, player.getTarget(), player);
	}

	private int getBaseHealing() {
		return handler.getHealthGained(simulatedAbilityName, player4);
	}

	private int getModifiedHealing() {
		return handler.getHealthGained(simulatedAbilityName, player3);
	}

	private int getBaseSelfHealing() {
		return handler.getHealthGained(simulatedAbilityName, player2);
	}

	private int getModifiedSelfHealing() {
		return handler.getHealthGained(simulatedAbilityName, player);
	}

	private double getBaseCastTime() {
		return handler.getCastTime(simulatedAbilityName, player2);
	}

	private double getModifiedCastTime() {
		return handler.getCastTime(simulatedAbilityName, player);
	}

	private int getBaseManaCost() {
		return handler.getManaPaid(simulatedAbilityName, player2);
	}

	private int getModifiedManaCost() {
		return handler.getManaPaid(simulatedAbilityName, player);
	}

	private double getBaseCooldown() {
		return handler.getCooldown(simulatedAbilityName, player2);
	}

	private double getModifiedCooldown() {
		return handler.getCooldown(simulatedAbilityName, player);
	}

	private double getBaseDuration() {
		return ((Duration) handler.getEffectDuration(simulatedAbilityName, player2.getTarget())).getSeconds();
	}

	private double getModifiedDuration() {
		return ((Duration) handler.getEffectDuration(simulatedAbilityName, player.getTarget())).getSeconds();
	}

	protected void assertStatConversion(String talentName, int rank, ToIntFunction<StatSummary> sourceStat, ToIntFunction<StatSummary> targetStat, int ratio) {
		var statsBefore = player.getStats();

		enableTalent(talentName, rank);

		var statsAfter = player.getStats();

		var sourceStatBefore = sourceStat.applyAsInt(statsBefore);
		var targetStatBefore = targetStat.applyAsInt(statsBefore);
		var targetStatAfter = targetStat.applyAsInt(statsAfter);
		var bonus = getPercentOf(ratio, sourceStatBefore);

		assertThat(targetStatAfter).isEqualTo(targetStatBefore + bonus);
	}

	protected void assertStaminaIsIncreasedByPct(String talentName, int rank, int pctIncrease) {
		assertStatIncreasedByPct(talentName, rank, StatSummary::getStamina, pctIncrease);
	}

	protected void assertIntellectIsIncreasedByPct(String talentName, int rank, int pctIncrease) {
		assertStatIncreasedByPct(talentName, rank, StatSummary::getIntellect, pctIncrease);
	}

	protected void assertSpiritIsIncreasedByPct(String talentName, int rank, int pctIncrease) {
		assertStatIncreasedByPct(talentName, rank, StatSummary::getSpirit, pctIncrease);
	}

	protected void assertSpiritIsReducedByPct(String talentName, int rank, int pctReduction) {
		assertStatIncreasedByPct(talentName, rank, StatSummary::getSpirit, -pctReduction);
	}

	protected void assertMaxHealthIsIncreasedByPct(String talentName, int rank, int pctIncrease) {
		assertStatIncreasedByPct(talentName, rank, StatSummary::getMaxHealth, pctIncrease);
	}

	protected void assertMaxManaIsIncreasedByPct(String talentName, int rank, int pctIncrease) {
		assertStatIncreasedByPct(talentName, rank, StatSummary::getMaxMana, pctIncrease);
	}

	private void assertStatIncreasedByPct(String talentName, int rank, ToIntFunction<StatSummary> stat, int pctIncrease) {
		var statBefore = stat.applyAsInt(player.getStats());

		enableTalent(talentName, rank);

		var statAfter = stat.applyAsInt(player.getStats());

		assertIsIncreasedByPct(statAfter, statBefore, pctIncrease);
	}

	protected void assertStatBonusIsIncreasedByPct(ToIntFunction<StatSummary> stat, int pctIncrease) {
		var base = stat.applyAsInt(statsAt(player, 0));
		var modifiedBonus = stat.applyAsInt(player.getStats()) - base;
		var baseBonus = stat.applyAsInt(player2.getStats()) - base;

		assertIsIncreasedByPct(modifiedBonus, baseBonus, pctIncrease);
	}

	protected void assertBonusStatConversion(ToIntFunction<StatSummary> sourceStat, ToIntFunction<StatSummary> targetStat, int ratio) {
		var base = targetStat.applyAsInt(statsAt(player, 0));
		var modifiedBonus = targetStat.applyAsInt(player.getStats()) - base;
		var baseBonus = targetStat.applyAsInt(player2.getStats()) - base;
		var modifiedSourceStat = sourceStat.applyAsInt(player.getStats());
		var convertedBonus = getPercentOf(ratio, modifiedSourceStat);

		assertIsIncreasedBy(modifiedBonus, baseBonus, convertedBonus);
	}

	protected void assertEnablingTalentTeachesAbility(String talentName, String abilityName) {
		assertThat(player.getAbility(abilityName)).isEmpty();
		enableTalent(talentName, 1);
		assertThat(player.getAbility(abilityName)).isPresent();
	}

	protected void setSimulationParams(CharacterClassId characterClassId, RaceId raceId, PhaseId phaseId) {
		this.characterClassId = characterClassId;
		this.raceId = raceId;
		this.phaseId = phaseId;
		this.partyMemberClassId = characterClassId;
		this.partyMemberRaceId = raceId;
	}

	protected void testingAoeAbility() {
		this.unmodifiedRollIdx = 5;
	}

	protected void testingHealingAbility() {
		addStaminaBonus(player3, 500);
		addStaminaBonus(player4, 500);

		setHealth(player3, 1);
		setHealth(player4, 1);

		player.setTarget(player3);
		player2.setTarget(player4);
	}

	private static final Set<String> HEALING_ABILITY_NAMES = Set.of(
			REJUVENATION,
			HEALING_TOUCH,
			REGROWTH,
			HOLY_LIGHT,
			FLASH_OF_LIGHT,
			HEALING_WAVE,
			CHAIN_HEAL
	);

	private static final Set<String> AOE_ABILITY_NAMES = Set.of(
			ARCANE_EXPLOSION,
			FLAMESTRIKE,
			CONE_OF_COLD
	);

	private static final Set<String> TALENT_ABILITY_NAMES = Set.of(
			HOLY_SHOCK,
			DIVINE_SPIRIT,
			MIND_FLAY
	);

	private boolean isHealingAbility(String abilityName) {
		return HEALING_ABILITY_NAMES.contains(abilityName);
	}

	private boolean isAoEgAbility(String abilityName) {
		return AOE_ABILITY_NAMES.contains(abilityName);
	}

	private void enableRequiredTalent(String abilityName) {
		if (TALENT_ABILITY_NAMES.contains(abilityName)) {
			enableTalent(player, abilityName, 1);
			enableTalent(player2, abilityName, 1);
		}
	}
}
