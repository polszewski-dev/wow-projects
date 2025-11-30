package wow.character.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.snapshot.*;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.character.PetType;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.SpellSchool;

import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.profession.ProfessionId.TAILORING;
import static wow.commons.model.profession.ProfessionSpecializationId.SPELLFIRE_TAILORING;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_LIFE_TAP;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 2023-04-28
 */
class CharacterCalculationServiceTest extends WowCharacterSpringTest {
	@Autowired
	CharacterCalculationService characterCalculationService;

	@ParameterizedTest
	@CsvSource({
			"Strength",
			"Strength%",
			"Agility",
			"Agility%",
			"Stamina",
			"Stamina%",
			"Intellect",
			"Intellect%",
			"Spirit",
			"Spirit%",
			"BaseStats",
			"BaseStats%",
			"MaxHealth",
			"MaxHealth%",
			"MaxMana",
			"MaxMana%",
	})
	void newAccumulatedBaseStats(String idStr) {
		var stats = characterCalculationService.newAccumulatedBaseStats(character);

		assertAccumulatedValue(idStr, 10, 10, null, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Haste%,      Spell",
			"Haste%,      OwnerHealthBelow40%",
			"Haste%,      OwnerHealthBelow70%",
			"HasteRating, Spell",
			"CastTime,    Shadow Bolt",
			"CastTime%,   HasCastTimeUnder10Sec",
			"CastTime%,   Shadow Bolt"
	})
	void newAccumulatedCastStats(String idStr, String conditionStr) {
		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var stats = characterCalculationService.newAccumulatedCastStats(character, ability, null);

		character.setHealthPct(Percent.of(30));

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Haste%, Physical",
			"Haste%, OwnerHealthBelow40%",
			"Haste%, OwnerHealthBelow70%",
	})
	void newAccumulatedCastStatsNotMatchingCondition(String idStr, String conditionStr) {
		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var stats = characterCalculationService.newAccumulatedCastStats(character, ability, null);

		assertAccumulatedValue(idStr, 10, 0, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"ManaCost,       Spell",
			"ManaCost,       Curse of Doom",
			"ManaCost,       Curses",
			"ManaCost,       Affliction",
			"ManaCost,       IsInstantCast",
			"ManaCost,       Shadow",
			"ManaCost%,      Spell",
			"ManaCost%,      Curse of Doom",
			"ManaCost%,      Curses",
			"ManaCost%,      Affliction",
			"ManaCost%,      IsInstantCast",
			"ManaCost%,      Shadow",
			"CostReduction%, Curse of Doom",
			"Power,          Spell",
			"Cooldown,       Curse of Doom",
			"Cooldown%,      Curse of Doom",
	})
	void newAccumulatedCostStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedCostStats(character, ability, null);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"CritTaken%,      SpellDamage",
			"DamageTaken,     Shadow",
			"DamageTaken,     Spell",
			"DamageTaken,     SpellDamage",
			"DamageTaken%,    Shadow",
			"DamageTaken%,    Spell",
			"DamageTaken%,    SpellDamage",
			"PowerTaken,      SpellDamage",
	})
	void newAccumulatedTargetStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedTargetStats(target, ability, SPELL_DAMAGE, SpellSchool.SHADOW);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@Test
	void newAccumulatedTargetStatsTargetHasPet() {
		var playerTarget = character;

		playerTarget.getBuild().setActivePet(PetType.VOIDWALKER);

		var idStr = "DamageTaken%";
		var conditionStr = "Spell & Voidwalker";
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedTargetStats(playerTarget, ability, SPELL_DAMAGE, SpellSchool.SHADOW);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Hit%,      Affliction",
			"Hit%,      Curse of Doom",
			"Hit%,      Curses",
			"Hit%,      Shadow",
			"Hit%,      Spell",
			"Hit%,      SpellDamage",
			"HitRating, Spell",
	})
	void newAccumulatedHitStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedHitStats(character, ability, target);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Duration,    Curse of Doom",
			"Duration,    Curses",
			"Duration%,   Curse of Doom",
			"Duration%,   Curses",
			"Haste%,      Spell",
			"HasteRating, Spell"
	})
	void newAccumulatedDurationStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedDurationStats(character, ability, target);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"ReceivedEffectDuration,  Curse of Doom",
			"ReceivedEffectDuration%, Curses",
	})
	void newAccumulatedReceivedEffectStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedReceivedEffectStats(target, ability);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Damage,                Shadow Bolt",
			"Damage,                Direct",
			"Damage,                SpellDamage",
			"Damage,                Undead",
			"Damage%,               Shadow Bolt",
			"Damage%,               Direct",
			"Damage%,               Succubus",
			"Damage%,               Shadow",
			"Damage%,               SpellDamage",
			"Damage%,               Undead",
			"Effect%,               Shadow Bolt",
			"Power,                 Spell",
			"Power,                 SpellDamage",
			"Power,                 SpellDamage & Shadow",
			"Power,                 SpellDamage & Undead",
			"Power%,                SpellDamage",
			"PowerCoeff%,           Shadow Bolt",
			"Crit%,                 Shadow Bolt",
			"Crit%,                 Destruction",
			"Crit%,                 Shadow",
			"Crit%,                 Spell",
			"Crit%,                 SpellDamage",
			"CritEffectMultiplier%, Destruction",
			"CritRating,            Spell",
			"CritRating,            SpellDamage",
			"CritEffect%,",
			"CritCoeff%,",
	})
	void newAccumulatedDirectComponentStats(String idStr, String conditionStr) {
		character.getBuild().setActivePet(PetType.SUCCUBUS);

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var directCommand = ability.getDirectCommands().getFirst();
		var stats = characterCalculationService.newAccumulatedDirectComponentStats(character, ability, target, SPELL_DAMAGE, directCommand);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Damage,                Curse of Doom",
			"Damage,                Curses",
			"Damage,                SpellDamage",
			"Damage,                Undead",
			"Damage%,               Curse of Doom",
			"Damage%,               Succubus",
			"Damage%,               Shadow",
			"Damage%,               SpellDamage",
			"Damage%,               Undead",
			"Effect%,               Curse of Doom",
			"Effect%,               Curses",
			"Power,                 Spell",
			"Power,                 SpellDamage",
			"Power,                 SpellDamage & Shadow",
			"Power,                 SpellDamage & Undead",
			"Power%,                SpellDamage",
			"PowerCoeff%,           Curse of Doom",
			"Crit%,                 Curse of Doom",
			"Crit%,                 Affliction",
			"Crit%,                 Shadow",
			"Crit%,                 Spell",
			"Crit%,                 SpellDamage",
			"CritEffectMultiplier%, Affliction",
			"CritRating,            Spell",
			"CritRating,            SpellDamage",
	})
	void newAccumulatedPeriodicComponentStats(String idStr, String conditionStr) {
		character.getBuild().setActivePet(PetType.SUCCUBUS);

		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var periodicComponent = ability.getAppliedEffect().getPeriodicComponent();
		var stats = characterCalculationService.newAccumulatedPeriodicComponentStats(character, ability, target, SPELL_DAMAGE, periodicComponent);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	private <T extends AccumulatedStats> void assertAccumulatedValue(String idStr, int value, int expectedValue, String condStr, T stats, ToDoubleBiFunction<T, AttributeId> valueAccessor) {
		var id = AttributeId.parse(idStr);
		var condition = AttributeCondition.parse(condStr);
		var attribute = Attribute.of(id, value, condition);

		var initialValue = valueAccessor.applyAsDouble(stats, id);

		stats.accumulateAttributes(List.of(attribute), 1);

		var actualValue = valueAccessor.applyAsDouble(stats, id);

		assertThat(actualValue - initialValue).isEqualTo(expectedValue);
	}

	@Test
	void getBaseStatsSnapshot() {
		character.resetEquipment();
		character.resetBuffs();

		var snapshot = characterCalculationService.getBaseStatsSnapshot(character);

		assertThat(snapshot.getStrength()).isEqualTo(50);
		assertThat(snapshot.getAgility()).isEqualTo(56);
		assertThat(snapshot.getStamina()).isEqualTo(88);
		assertThat(snapshot.getIntellect()).isEqualTo(131);
		assertThat(snapshot.getSpirit()).isEqualTo(136);
		assertThat(snapshot.getMaxHealth()).isEqualTo(4315);
		assertThat(snapshot.getMaxMana()).isEqualTo(4414);
	}

	@Test
	void getSpellCastSnapshot() {
		character.resetEquipment();

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var snapshot = characterCalculationService.getSpellCastSnapshot(character, ability, (Character) null);

		assertThat(snapshot.getHastePct()).isZero();
		assertThat(snapshot.getCastTime()).isEqualTo(2.5);
		assertThat(snapshot.getGcd()).isEqualTo(1.5);
		assertThat(snapshot.isInstantCast()).isFalse();
	}

	@Test
	void getSpellCostSnapshot() {
		character.resetEquipment();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);
		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var snapshot = characterCalculationService.getSpellCostSnapshot(character, ability, null, baseStats);

		assertThat(snapshot.getResourceType()).isEqualTo(ResourceType.MANA);
		assertThat(snapshot.getCost()).isEqualTo(399);
		assertThat(snapshot.getCostUnreduced()).isEqualTo(399);
		assertThat(snapshot.getCooldown()).isZero();
	}

	@Test
	void getSpellCostSnapshot2() {
		character.resetEquipment();

		character.getTalents().loadFromTalentLink("https://www.wowhead.com/tbc/talent-calc/warlock/550220200203--55500051221001303025");

		assertThat(character.hasTalent(IMPROVED_LIFE_TAP)).isTrue();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);
		var ability = character.getAbility(LIFE_TAP).orElseThrow();
		var snapshot = characterCalculationService.getSpellCostSnapshot(character, ability, null, baseStats);

		assertThat(snapshot.getResourceType()).isEqualTo(ResourceType.HEALTH);
		assertThat(snapshot.getCost()).isEqualTo(698);
		assertThat(snapshot.getCostUnreduced()).isEqualTo(698);
		assertThat(snapshot.getCooldown()).isZero();
	}

	@Test
	void getSpellCostSnapshot3() {
		character.resetEquipment();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);
		var ability = character.getAbility(SHADOWBURN).orElseThrow();
		var snapshot = characterCalculationService.getSpellCostSnapshot(character, ability, null, baseStats);

		assertThat(snapshot.getResourceType()).isEqualTo(ResourceType.MANA);
		assertThat(snapshot.getCost()).isEqualTo(489);
		assertThat(snapshot.getCostUnreduced()).isEqualTo(489);
		assertThat(snapshot.getCooldown()).isEqualTo(15);
	}

	@Test
	void getSpellHitPct() {
		character.resetEquipment();

		character.getTalents().loadFromTalentLink("https://www.wowhead.com/tbc/talent-calc/warlock/550220200203--55500051221001303025");

		var ability = character.getAbility(CURSE_OF_AGONY).orElseThrow();
		var hitPct = characterCalculationService.getSpellHitPct(character, ability, target);

		assertThat(hitPct).isEqualTo(96);
	}

	@Test
	void getEffectDurationSnapshotEffectZero() {
		var priest = getCharacter(PRIEST, UNDEAD);

		priest.resetEquipment();

		var ability = priest.getAbility(SHADOW_WORD_PAIN).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(priest, ability, priest.getTarget());

		assertThat(durationSnapshot.getDuration()).isEqualTo(Duration.seconds(24));
		assertThat(durationSnapshot.getNumTicks()).isEqualTo(8);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(Duration.seconds(3));
	}

	@Test
	void getEffectDurationSnapshotEffectSomeHaste() {
		var priest = getCharacter(PRIEST, UNDEAD);

		equipGearSet(character);

		var ability = priest.getAbility(SHADOW_WORD_PAIN).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(priest, ability, priest.getTarget());

		assertThat(durationSnapshot.getDuration()).isEqualTo(Duration.seconds(24));
		assertThat(durationSnapshot.getNumTicks()).isEqualTo(8);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(Duration.seconds(3));
	}

	@Test
	void getEffectDurationSnapshotChannelZeroHaste() {
		character.resetEquipment();

		var ability = character.getAbility(DRAIN_LIFE).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(character, ability, target);

		assertThat(durationSnapshot.getDuration()).isEqualTo(Duration.seconds(5));
		assertThat(durationSnapshot.getNumTicks()).isEqualTo(5);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(Duration.seconds(1));
	}

	@Test
	void getEffectDurationSnapshotChannelSomeHaste() {
		equipGearSet(character);

		var ability = character.getAbility(DRAIN_LIFE).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(character, ability, target);

		assertThat(durationSnapshot.getDuration()).isEqualTo(Duration.millis(3785));
		assertThat(durationSnapshot.getNumTicks()).isEqualTo(5);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(Duration.millis(757));
	}

	@Test
	void getDirectSpellDamageSnapshot() {
		character.resetEquipment();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);

		assertThat(baseStats.getIntellect()).isEqualTo(203);

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var directCommand = ability.getDirectCommands().getFirst();
		var snapshot = characterCalculationService.getDirectSpellDamageSnapshot(character, ability, target, directCommand, baseStats);

		assertThat(snapshot.getCritPct()).isEqualTo(15.87, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2);
		assertThat(snapshot.getAmount()).isZero();
		assertThat(snapshot.getAmountPct()).isEqualTo(25);
		assertThat(snapshot.getPower()).isEqualTo(370);
		assertThat(snapshot.getPowerPct()).isZero();
		assertThat(snapshot.getCoeff()).isEqualTo(105.71, PRECISION);
	}

	@Test
	void getDirectSpellDamageSnapshotIntToSpConversion() {
		character.resetEquipment();

		character.resetProfessions();
		character.addProfessionMaxLevel(TAILORING, SPELLFIRE_TAILORING);

		character.equip(getItem("Spellfire Robe"));
		character.equip(getItem("Spellfire Gloves"));
		character.equip(getItem("Spellfire Belt"));

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);

		assertThat(baseStats.getIntellect()).isEqualTo(253);

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var directCommand = ability.getDirectCommands().getFirst();
		var snapshot = characterCalculationService.getDirectSpellDamageSnapshot(character, ability, target, directCommand, baseStats);

		assertThat(snapshot.getCritPct()).isEqualTo(19.62, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2);
		assertThat(snapshot.getAmount()).isZero();
		assertThat(snapshot.getAmountPct()).isEqualTo(25);
		assertThat(snapshot.getPower()).isEqualTo(387);//370 + 253 * 0.07
		assertThat(snapshot.getPowerPct()).isZero();
		assertThat(snapshot.getCoeff()).isEqualTo(105.71, PRECISION);
	}

	@Test
	void getPeriodicSpellDamageSnapshot() {
		character.resetEquipment();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);

		var ability = character.getAbility(CURSE_OF_AGONY).orElseThrow();
		var snapshot = characterCalculationService.getPeriodicSpellDamageSnapshot(character, ability, target, baseStats);

		assertThat(snapshot.getAmount()).isZero();
		assertThat(snapshot.getAmountPct()).isEqualTo(25);
		assertThat(snapshot.getPower()).isEqualTo(370);
		assertThat(snapshot.getPowerPct()).isZero();
		assertThat(snapshot.getCoeff()).isEqualTo(120);
	}

	@Test
	void getStatSummary() {
		equipGearSet(character);

		var snapshot = characterCalculationService.getStatSummary(character);

		assertThat(snapshot.getStrength()).isEqualTo(77);
		assertThat(snapshot.getAgility()).isEqualTo(83);
		assertThat(snapshot.getStamina()).isEqualTo(818);
		assertThat(snapshot.getIntellect()).isEqualTo(632);
		assertThat(snapshot.getSpirit()).isEqualTo(245);
		assertThat(snapshot.getMaxHealth()).isEqualTo(11834);
		assertThat(snapshot.getMaxMana()).isEqualTo(12155);
		assertThat(snapshot.getSpellPower()).isEqualTo(1277);
		assertThat(snapshot.getSpellDamage()).isEqualTo(1604);
		assertThat(snapshot.getSpellDamageBySchool()).isEqualTo(Map.ofEntries(
				Map.entry(SpellSchool.FROST, 1738),
				Map.entry(SpellSchool.FIRE, 1684),
				Map.entry(SpellSchool.ARCANE, 1604),
				Map.entry(SpellSchool.SHADOW, 1738),
				Map.entry(SpellSchool.HOLY, 1604),
				Map.entry(SpellSchool.NATURE, 1604)
		));
		assertThat(snapshot.getSpellHitPctBonus()).isEqualTo(16.39, PRECISION);
		assertThat(snapshot.getSpellHitPct()).isEqualTo(98.99, PRECISION);
		assertThat(snapshot.getSpellCritPct()).isEqualTo(27.83, PRECISION);
		assertThat(snapshot.getSpellHastePct()).isEqualTo(31.96, PRECISION);
		assertThat(snapshot.getSpellHitRating()).isEqualTo(169);
		assertThat(snapshot.getSpellCritRating()).isEqualTo(270);
		assertThat(snapshot.getOutOfCombatHealthRegen()).isZero();
		assertThat(snapshot.getInCombatHealthRegen()).isZero();
		assertThat(snapshot.getUninterruptedManaRegen()).isEqualTo(288);
		assertThat(snapshot.getInterruptedManaRegen()).isZero();
	}

	private double getValue(AccumulatedBaseStats stats, AttributeId id) {
		return switch (id) {
			case STRENGTH -> stats.getStrength();
			case STRENGTH_PCT -> stats.getStrengthPct();
			case AGILITY -> stats.getAgility();
			case AGILITY_PCT -> stats.getAgilityPct();
			case STAMINA -> stats.getStamina();
			case STAMINA_PCT -> stats.getStaminaPct();
			case INTELLECT -> stats.getIntellect();
			case INTELLECT_PCT -> stats.getIntellectPct();
			case SPIRIT -> stats.getSpirit();
			case SPIRIT_PCT -> stats.getSpiritPct();
			case BASE_STATS -> stats.getBaseStats();
			case BASE_STATS_PCT -> stats.getBaseStatsPct();
			case MAX_HEALTH -> stats.getMaxHealth();
			case MAX_HEALTH_PCT -> stats.getMaxHealthPct();
			case MAX_MANA -> stats.getMaxMana();
			case MAX_MANA_PCT -> stats.getMaxManaPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedCastStats stats, AttributeId id) {
		return switch (id) {
			case HASTE_RATING -> stats.getHasteRating();
			case HASTE_PCT -> stats.getHastePct();
			case CAST_TIME -> stats.getCastTime();
			case CAST_TIME_PCT -> stats.getCastTimePct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedCostStats stats, AttributeId id) {
		return switch (id) {
			case MANA_COST -> stats.getManaCost();
			case MANA_COST_PCT -> stats.getManaCostPct();
			case ENERGY_COST -> stats.getEnergyCost();
			case ENERGY_COST_PCT -> stats.getEnergyCostPct();
			case RAGE_COST -> stats.getRageCost();
			case RAGE_COST_PCT -> stats.getRageCostPct();
			case HEALTH_COST -> stats.getHealthCost();
			case HEALTH_COST_PCT -> stats.getHealthCostPct();
			case COST_REDUCTION_CT -> stats.getCostReductionPct();
			case POWER -> stats.getPower();
			case COOLDOWN -> stats.getCooldown();
			case COOLDOWN_PCT -> stats.getCooldownPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedTargetStats stats, AttributeId id) {
		return switch (id) {
			case DAMAGE_TAKEN -> stats.getAmountTaken();
			case DAMAGE_TAKEN_PCT -> stats.getAmountTakenPct();
			case POWER_TAKEN -> stats.getPowerTaken();
			case CRIT_TAKEN_PCT -> stats.getCritTakenPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedHitStats stats, AttributeId id) {
		return switch (id) {
			case HIT_RATING -> stats.getHitRating();
			case HIT_PCT -> stats.getHitPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedDurationStats stats, AttributeId id) {
		return switch (id) {
			case DURATION -> stats.getDuration();
			case DURATION_PCT -> stats.getDurationPct();
			case HASTE_RATING -> stats.getHasteRating();
			case HASTE_PCT -> stats.getHastePct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedReceivedEffectStats stats, AttributeId id) {
		return switch (id) {
			case RECEIVED_EFFECT_DURATION -> stats.getReceivedEffectDuration();
			case RECEIVED_EFFECT_DURATION_PCT -> stats.getReceivedEffectDurationPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedSpellStats stats, AttributeId id) {
		return switch (id) {
			case DAMAGE -> stats.getAmount();
			case DAMAGE_PCT -> stats.getAmountPct();
			case EFFECT_PCT -> stats.getEffectPct();
			case POWER -> stats.getPower();
			case POWER_PCT -> stats.getPowerPct();
			case POWER_COEFFICIENT_PCT -> stats.getPowerCoeffPct();
			case CRIT_RATING -> stats.getCritRating();
			case CRIT_PCT -> stats.getCritPct();
			case CRIT_EFFECT_PCT -> stats.getCritEffectPct();
			case CRIT_EFFECT_MULTIPLIER_PCT -> stats.getCritEffectMultiplierPct();
			case CRIT_COEFF_PCT -> stats.getCritCoeffPct();
			default -> throw new IllegalArgumentException();
		};
	}

	PlayerCharacter character;
	Character target;

	@BeforeEach
	void setup() {
		character = getCharacter();
		target = character.getTarget();
	}
}