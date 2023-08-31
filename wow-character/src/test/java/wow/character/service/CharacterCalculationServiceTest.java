package wow.character.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.snapshot.*;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PowerType;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.character.PetType;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentId;

import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.spell.AbilityId.*;

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
		var stats = characterCalculationService.newAccumulatedCastStats(character, ability);

		stats.getConditionArgs().setOwnerHealth(Percent.of(30));

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
		var stats = characterCalculationService.newAccumulatedCastStats(character, ability);

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
			"ManaCost,       HasDamagingComponent",
			"ManaCost%,      Spell",
			"ManaCost%,      Curse of Doom",
			"ManaCost%,      Curses",
			"ManaCost%,      Affliction",
			"ManaCost%,      IsInstantCast",
			"ManaCost%,      Shadow",
			"ManaCost%,      HasDamagingComponent",
			"CostReduction%, Curse of Doom",
			"Power,          Spell",
			"Cooldown,       Curse of Doom",
			"Cooldown%,      Curse of Doom",
	})
	void newAccumulatedCostStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedCostStats(character, ability);

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
			"ReceivedEffectDuration,  Curse of Doom",
			"ReceivedEffectDuration%, Curses",
	})
	void newAccumulatedTargetStats(String idStr, String conditionStr) {
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedTargetStats(target, ability, PowerType.SPELL_DAMAGE, SpellSchool.SHADOW);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@Test
	void newAccumulatedTargetStatsTargetHasPet() {
		var playerTarget = character.copy();

		playerTarget.getBuild().setActivePet(PetType.VOIDWALKER);

		var idStr = "DamageTaken%";
		var conditionStr = "Spell & Voidwalker";
		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var stats = characterCalculationService.newAccumulatedTargetStats(playerTarget, ability, PowerType.SPELL_DAMAGE, SpellSchool.SHADOW);

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
			"CritDamageMultiplier%, Destruction",
			"CritRating,            Spell",
			"CritRating,            SpellDamage",
			"CritDamage%,",
			"CritCoeff%,",
	})
	void newAccumulatedDirectComponentStats(String idStr, String conditionStr) {
		character.getBuild().setActivePet(PetType.SUCCUBUS);

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var directComponent = ability.getDirectComponents().get(0);
		var stats = characterCalculationService.newAccumulatedDirectComponentStats(character, ability, target, directComponent);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	@ParameterizedTest
	@CsvSource({
			"Damage,                Curse of Doom",
			"Damage,                Curses",
			"Damage,                Periodic",
			"Damage,                SpellDamage",
			"Damage,                Undead",
			"Damage%,               Curse of Doom",
			"Damage%,               Periodic",
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
			"CritDamageMultiplier%, Affliction",
			"CritRating,            Spell",
			"CritRating,            SpellDamage",
	})
	void newAccumulatedPeriodicComponentStats(String idStr, String conditionStr) {
		character.getBuild().setActivePet(PetType.SUCCUBUS);

		var ability = character.getAbility(CURSE_OF_DOOM).orElseThrow();
		var periodicComponent = ability.getAppliedEffect().getPeriodicComponent();
		var stats = characterCalculationService.newAccumulatedPeriodicComponentStats(character, ability, target, periodicComponent);

		assertAccumulatedValue(idStr, 10, 10, conditionStr, stats, this::getValue);
	}

	private <T extends AccumulatedStats> void assertAccumulatedValue(String idStr, int value, int expectedValue, String condStr, T stats, ToDoubleBiFunction<T, PrimitiveAttributeId> valueAccessor) {
		var id = PrimitiveAttributeId.parse(idStr);
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
		assertThat(snapshot.getMaxMana()).isEqualTo(4429);
	}

	@Test
	void getSpellCastSnapshot() {
		character.resetEquipment();

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var snapshot = characterCalculationService.getSpellCastSnapshot(character, ability);

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
		var snapshot = characterCalculationService.getSpellCostSnapshot(character, ability, baseStats);

		assertThat(snapshot.getResourceType()).isEqualTo(ResourceType.MANA);
		assertThat(snapshot.getCost()).isEqualTo(399);
		assertThat(snapshot.getCostUnreduced()).isEqualTo(399);
		assertThat(snapshot.getCooldown()).isZero();
	}

	@Test
	void getSpellCostSnapshot2() {
		character.resetEquipment();

		character.getTalents().loadFromTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=5502202002030000000000000000000000000000000555000512210013030250");

		assertThat(character.hasTalent(TalentId.IMPROVED_LIFE_TAP)).isTrue();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);
		var ability = character.getAbility(LIFE_TAP).orElseThrow();
		var snapshot = characterCalculationService.getSpellCostSnapshot(character, ability, baseStats);

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
		var snapshot = characterCalculationService.getSpellCostSnapshot(character, ability, baseStats);

		assertThat(snapshot.getResourceType()).isEqualTo(ResourceType.MANA);
		assertThat(snapshot.getCost()).isEqualTo(489);
		assertThat(snapshot.getCostUnreduced()).isEqualTo(489);
		assertThat(snapshot.getCooldown()).isEqualTo(15);
	}

	@Test
	void getSpellHitPct() {
		character.resetEquipment();

		character.getTalents().loadFromTalentLink("https://legacy-wow.com/tbc-talents/warlock-talents/?tal=5502202002030000000000000000000000000000000555000512210013030250");

		var ability = character.getAbility(CURSE_OF_AGONY).orElseThrow();
		var hitPct = characterCalculationService.getSpellHitPct(character, ability, target);

		assertThat(hitPct).isEqualTo(96);
	}

	@Test
	void getEffectDurationSnapshotEffect() {
		var priest = getCharacter(PRIEST, UNDEAD);

		priest.resetEquipment();

		var ability = priest.getAbility(SHADOW_WORD_PAIN).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(priest, ability, priest.getTarget());

		assertThat(durationSnapshot.getDuration()).isEqualTo(24);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(3);
	}

	@Test
	void getEffectDurationSnapshotChannelZeroHaste() {
		character.resetEquipment();

		var ability = character.getAbility(DRAIN_LIFE).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(character, ability, target);

		assertThat(durationSnapshot.getDuration()).isEqualTo(5);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(1);
	}

	@Test
	void getEffectDurationSnapshotChannelSomeHaste() {
		character.setEquipment(getEquipment());

		var ability = character.getAbility(DRAIN_LIFE).orElseThrow();
		var durationSnapshot = characterCalculationService.getEffectDurationSnapshot(character, ability, target);

		assertThat(durationSnapshot.getDuration()).isEqualTo(3.93, PRECISION);
		assertThat(durationSnapshot.getTickInterval()).isEqualTo(0.78, PRECISION);
	}

	@Test
	void getDirectSpellDamageSnapshot() {
		character.resetEquipment();

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);

		assertThat(baseStats.getIntellect()).isEqualTo(203);

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var directComponent = ability.getDirectComponents().get(0);
		var snapshot = characterCalculationService.getDirectSpellDamageSnapshot(character, ability, target, directComponent, baseStats);

		assertThat(snapshot.getCritPct()).isEqualTo(15.83, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2);
		assertThat(snapshot.getDamage()).isZero();
		assertThat(snapshot.getDamagePct()).isEqualTo(25);
		assertThat(snapshot.getPower()).isEqualTo(370);
		assertThat(snapshot.getPowerPct()).isZero();
		assertThat(snapshot.getCoeff()).isEqualTo(105.71, PRECISION);
	}

	@Test
	void getDirectSpellDamageSnapshotIntToSpConversion() {
		character.resetEquipment();

		var tailoring = character.getGameVersion().getProfession(ProfessionId.TAILORING);
		var spellfireTailoring = tailoring.getSpecialization(ProfessionSpecializationId.SPELLFIRE_TAILORING);

		character.resetProfessions();
		character.setProfessions(List.of(
				new CharacterProfession(tailoring, spellfireTailoring, 375)
		));

		character.equip(getItem("Spellfire Robe"));
		character.equip(getItem("Spellfire Gloves"));
		character.equip(getItem("Spellfire Belt"));

		var baseStats = characterCalculationService.getBaseStatsSnapshot(character);

		assertThat(baseStats.getIntellect()).isEqualTo(253);

		var ability = character.getAbility(SHADOW_BOLT).orElseThrow();
		var directComponent = ability.getDirectComponents().get(0);
		var snapshot = characterCalculationService.getDirectSpellDamageSnapshot(character, ability, target, directComponent, baseStats);

		assertThat(snapshot.getCritPct()).isEqualTo(19.56, PRECISION);
		assertThat(snapshot.getCritCoeff()).isEqualTo(2);
		assertThat(snapshot.getDamage()).isZero();
		assertThat(snapshot.getDamagePct()).isEqualTo(25);
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

		assertThat(snapshot.getDamage()).isZero();
		assertThat(snapshot.getDamagePct()).isEqualTo(25);
		assertThat(snapshot.getPower()).isEqualTo(370);
		assertThat(snapshot.getPowerPct()).isZero();
		assertThat(snapshot.getCoeff()).isEqualTo(120);
	}

	@Test
	void getStatSummary() {
		character.setEquipment(getEquipment());

		var snapshot = characterCalculationService.getStatSummary(character);

		assertThat(snapshot.getStrength()).isEqualTo(77);
		assertThat(snapshot.getAgility()).isEqualTo(83);
		assertThat(snapshot.getStamina()).isEqualTo(797);
		assertThat(snapshot.getIntellect()).isEqualTo(620);
		assertThat(snapshot.getSpirit()).isEqualTo(245);
		assertThat(snapshot.getMaxHealth()).isEqualTo(11618);
		assertThat(snapshot.getMaxMana()).isEqualTo(11984);
		assertThat(snapshot.getSpellPower()).isEqualTo(1170);
		assertThat(snapshot.getSpellDamage()).isEqualTo(1616);
		assertThat(snapshot.getSpellDamageBySchool()).isEqualTo(Map.ofEntries(
				Map.entry(SpellSchool.FROST, 1750),
				Map.entry(SpellSchool.FIRE, 1696),
				Map.entry(SpellSchool.ARCANE, 1616),
				Map.entry(SpellSchool.SHADOW, 1750),
				Map.entry(SpellSchool.HOLY, 1616),
				Map.entry(SpellSchool.NATURE, 1616)
		));
		assertThat(snapshot.getSpellHitPctBonus()).isEqualTo(15.99, PRECISION);
		assertThat(snapshot.getSpellHitPct()).isEqualTo(98.99, PRECISION);
		assertThat(snapshot.getSpellCritPct()).isEqualTo(30.30, PRECISION);
		assertThat(snapshot.getSpellHastePct()).isEqualTo(27.03, PRECISION);
		assertThat(snapshot.getSpellHitRating()).isEqualTo(164);
		assertThat(snapshot.getSpellCritRating()).isEqualTo(331);
		assertThat(snapshot.getSpellHasteRating()).isEqualTo(426);
	}

	private double getValue(AccumulatedBaseStats stats, PrimitiveAttributeId id) {
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

	private double getValue(AccumulatedCastStats stats, PrimitiveAttributeId id) {
		return switch (id) {
			case HASTE_RATING -> stats.getHasteRating();
			case HASTE_PCT -> stats.getHastePct();
			case CAST_TIME -> stats.getCastTime();
			case CAST_TIME_PCT -> stats.getCastTimePct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedCostStats stats, PrimitiveAttributeId id) {
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

	private double getValue(AccumulatedTargetStats stats, PrimitiveAttributeId id) {
		return switch (id) {
			case DAMAGE_TAKEN -> stats.getDamageTaken();
			case DAMAGE_TAKEN_PCT -> stats.getDamageTakenPct();
			case POWER_TAKEN -> stats.getPowerTaken();
			case CRIT_TAKEN_PCT -> stats.getCritTakenPct();
			case RECEIVED_EFFECT_DURATION -> stats.getReceivedEffectDuration();
			case RECEIVED_EFFECT_DURATION_PCT -> stats.getReceivedEffectDurationPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedHitStats stats, PrimitiveAttributeId id) {
		return switch (id) {
			case HIT_RATING -> stats.getHitRating();
			case HIT_PCT -> stats.getHitPct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedDurationStats stats, PrimitiveAttributeId id) {
		return switch (id) {
			case DURATION -> stats.getDuration();
			case DURATION_PCT -> stats.getDurationPct();
			case HASTE_RATING -> stats.getHasteRating();
			case HASTE_PCT -> stats.getHastePct();
			default -> throw new IllegalArgumentException();
		};
	}

	private double getValue(AccumulatedSpellStats stats, PrimitiveAttributeId id) {
		return switch (id) {
			case DAMAGE -> stats.getDamage();
			case DAMAGE_PCT -> stats.getDamagePct();
			case EFFECT_PCT -> stats.getEffectPct();
			case POWER -> stats.getPower();
			case POWER_PCT -> stats.getPowerPct();
			case POWER_COEFFICIENT_PCT -> stats.getPowerCoeffPct();
			case CRIT_RATING -> stats.getCritRating();
			case CRIT_PCT -> stats.getCritPct();
			case CRIT_DAMAGE_PCT -> stats.getCritDamagePct();
			case CRIT_DAMAGE_MULTIPLIER_PCT -> stats.getCritDamageMultiplierPct();
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