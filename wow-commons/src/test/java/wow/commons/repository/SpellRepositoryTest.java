package wow.commons.repository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.condition.ConditionOperator;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.buff.BuffExclusionGroup;
import wow.commons.model.buff.BuffId;
import wow.commons.model.buff.BuffType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.*;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.buff.BuffCategory.RAID_BUFF;
import static wow.commons.model.categorization.PveRole.CASTER_DPS;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.effect.component.EventAction.REMOVE_CHARGE;
import static wow.commons.model.effect.component.EventAction.TRIGGER_SPELL;
import static wow.commons.model.effect.component.EventType.*;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.GameVersionId.VANILLA;
import static wow.commons.model.pve.PhaseId.*;
import static wow.commons.model.spell.AbilityId.CURSE_OF_EXHAUSTION;
import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.spell.Conversion.From.*;
import static wow.commons.model.spell.Conversion.To.*;
import static wow.commons.model.spell.SpellSchool.*;
import static wow.commons.model.talent.TalentId.*;
import static wow.commons.model.talent.TalentTree.AFFLICTION;
import static wow.commons.model.talent.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class SpellRepositoryTest extends WowCommonsSpringTest {
	@Test
	void abilityInfo() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);

		assertThat(ability.getRankedAbilityId()).isEqualTo(new AbilityIdAndRank(SHADOW_BOLT, 11));
		assertThat(ability.getAbilityId()).isEqualTo(SHADOW_BOLT);
		assertThat(ability.getRank()).isEqualTo(11);
		assertThat(ability.getTalentTree()).isEqualTo(ability.getTalentTree()).isEqualTo(DESTRUCTION);
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT,      11, WARLOCK, 69,       ,           , ",
			"SHADOWBURN,        8, WARLOCK, 70,       , SHADOWBURN, ",
			"SOUL_LINK,         0, WARLOCK,  1,       ,  SOUL_LINK,  Imp+Voidwalker+Succubus+Incubus+Felhunter+Felguard+Enslaved",
			"DEVOURING_PLAGUE,  7, PRIEST,  68, UNDEAD,           , ",
	})
	void abilityCharacterRestriction(AbilityId id, int rank, CharacterClassId characterClassId, int level, RaceId raceId, TalentId talentId, String petTypes) {
		var ability = getClassAbility(id, rank, TBC_P5);
		var characterRestriction = ability.getCharacterRestriction();

		assertThat(characterRestriction.characterClassIds()).isEqualTo(List.of(characterClassId));
		assertThat(characterRestriction.level()).isEqualTo(level);
		assertThat(characterRestriction.raceIds()).isEqualTo(raceId != null ? List.of(raceId) : List.of());
		assertThat(characterRestriction.talentId()).isEqualTo(talentId);

		var activePets = petTypes != null ? Stream.of(petTypes.split("\\+")).map(PetType::parse).toList() : List.of();

		assertThat(characterRestriction.activePet()).isEqualTo(activePets);
	}

	@Test
	void abilityTimeRestriction() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);
		var timeRestriction = ability.getTimeRestriction();

		assertThat(timeRestriction.versions()).isEqualTo(List.of(TBC));
		assertThat(timeRestriction.phaseId()).isNull();
	}

	@Test
	void abilityDescription() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);
		var description = ability.getDescription();

		assertThat(description.name()).isEqualTo("Shadow Bolt");
		assertThat(description.icon()).isEqualTo("spell_shadow_shadowbolt");
		assertThat(description.tooltip()).isEqualTo("Sends a shadowy bolt at the enemy, causing 544 to 607 Shadow damage.");
	}

	@Test
	void abilityCategory() {
		var ability = getClassAbility(CURSE_OF_AGONY, 7, TBC_P5);

		assertThat(ability.getCategory()).isEqualTo(AbilityCategory.CURSES);
	}

	@Test
	void abilitySpellInfo() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);

		assertThat(ability.getId()).isEqualTo(27209);
		assertThat(ability.getCooldown()).isEqualTo(Duration.ZERO);
		assertThat(ability.getRange()).isEqualTo(30);
		assertThat(ability.getRequiredEffect()).isNull();
		assertThat(ability.getEffectRemovedOnHit()).isNull();
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT, 11, 3, false, false",
			"DRAIN_LIFE, 8, 5, true, false",
			"AMPLIFY_CURSE, 0, 0, false, true",
	})
	void abilityCastInfo(AbilityId id, int rank, double castTime, boolean channeled, boolean ignoresGcd) {
		var ability = getClassAbility(id, rank, TBC_P5);
		var castInfo = ability.getCastInfo();

		assertThat(castInfo.castTime()).isEqualTo(Duration.seconds(castTime));
		assertThat(castInfo.channeled()).isEqualTo(channeled);
		assertThat(castInfo.ignoresGcd()).isEqualTo(ignoresGcd);
	}

	@ParameterizedTest
	@CsvSource({
			"SHADOW_BOLT,      11, MANA,   420,  0,  0,           ",
			"LIFE_TAP,          7, HEALTH, 582,  0, 80,           ",
			"SUMMON_VOIDWALKER, 0, MANA,     0, 80,  0, SOUL_SHARD",
	})
	void abilityCost(AbilityId id, int rank, ResourceType resourceType, int amount, int baseStatPct, double coeffValue, Reagent reagent) {
		var ability = getClassAbility(id, rank, TBC_P5);
		var cost = ability.getCost();

		assertThat(cost.resourceType()).isEqualTo(resourceType);
		assertThat(cost.amount()).isEqualTo(amount);
		assertThat(cost.baseStatPct()).isEqualTo(Percent.of(baseStatPct));
		assertCoefficient(cost.coefficient(), coeffValue, null);
		assertThat(cost.reagent()).isEqualTo(reagent);
	}

	@Test
	void abilityDirectComponent() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);
		var index = 0;
		var directComponent = ability.getDirectComponents().get(index);

		assertThat(directComponent.target()).isEqualTo(SpellTarget.ENEMY);
		assertThat(directComponent.type()).isEqualTo(ComponentType.DAMAGE);
		assertCoefficient(directComponent.coefficient(), 85.71, SHADOW);
		assertThat(directComponent.min()).isEqualTo(544);
		assertThat(directComponent.max()).isEqualTo(607);
		assertThat(directComponent.bolt()).isTrue();
	}

	@Test
	void abilityDirectBonus() {
		var ability = getClassAbility(INCINERATE, 2, TBC_P5);
		var index = 0;
		var bonus = ability.getDirectComponents().get(index).bonus();

		assertThat(bonus.min()).isEqualTo(111);
		assertThat(bonus.max()).isEqualTo(128);
		assertThat(bonus.requiredEffect()).isEqualTo(AbilityId.IMMOLATE);
	}

	@Test
	void abilityConversion() {
		var ability = getClassAbility(LIFE_TAP, 7, TBC_P5);
		var conversion = ability.getConversion();

		assertThat(conversion.condition().isEmpty()).isTrue();
		assertThat(conversion.from()).isEqualTo(HEALTH_PAID);
		assertThat(conversion.to()).isEqualTo(MANA);
		assertThat(conversion.ratioPct()).isEqualTo(Percent._100);
		assertThat(conversion.isLocal()).isTrue();
	}

	@Test
	void abilityEffectApplication() {
		var ability = getClassAbility(CORRUPTION, 8, TBC_P5);
		var effectApplication = ability.getEffectApplication();

		assertEffectApplication(ability, SpellTarget.ENEMY, 18, 1, 1, 1);

		assertThat(effectApplication.effect().getEffectId()).isEqualTo(ability.getId()).isEqualTo(27216);
	}

	@Test
	void abilitySpell() {
		var spell = getSpell(110025441, TBC_P5);

		assertThat(spell.getId()).isEqualTo(110025441);
		assertThat(spell.getName()).isEqualTo("Feedback - triggered");

		var directComponent = spell.getDirectComponents().get(0);

		assertThat(directComponent.target()).isEqualTo(SpellTarget.ATTACKER);
		assertThat(directComponent.type()).isEqualTo(ComponentType.MANA_DRAIN);
		assertCoefficient(directComponent.coefficient(), 0, SHADOW);
		assertThat(directComponent.min()).isEqualTo(165);
		assertThat(directComponent.max()).isEqualTo(165);
		assertThat(directComponent.bolt()).isFalse();

		var conversion = spell.getConversion();

		assertThat(conversion.condition().isEmpty()).isTrue();
		assertThat(conversion.from()).isEqualTo(MANA_DRAINED);
		assertThat(conversion.to()).isEqualTo(DAMAGE_ON_TARGET);
		assertThat(conversion.ratioPct()).isEqualTo(Percent._100);
		assertThat(conversion.isLocal()).isTrue();
	}

	@Test
	void abilityEffect() {
		var effect = getEffect(25311, VANILLA_P6);

		assertThat(effect.getEffectId()).isEqualTo(25311);
		assertThat(effect.getName()).isEqualTo("Corruption");
		assertThat(effect.getTimeRestriction().versions()).isEqualTo(List.of(VANILLA));
		assertThat(effect.getTimeRestriction().phaseId()).isEqualTo(VANILLA_P5);
		assertThat(effect.getMaxStacks()).isEqualTo(1);
		assertThat(effect.getIcon()).isEqualTo("spell_shadow_abominationexplosion");
		assertThat(effect.getTooltip()).isEqualTo("Corrupts the target, causing 822 Shadow damage over 18 sec.");
	}

	@Test
	void abilityEffectPeriodic() {
		var effect = getEffect(25311, VANILLA_P6);

		assertThat(effect.getName()).isEqualTo("Corruption");

		var periodicComponent = effect.getPeriodicComponent();

		assertPeriodicComponent(periodicComponent, ComponentType.DAMAGE, 100, SHADOW, 822, 6, TickScheme.DEFAULT);
		assertThat(effect.getTickInterval()).isEqualTo(Duration.seconds(3));
	}

	@Test
	void abilityEffectPeriodicTickScheme() {
		var effect = getEffect(27218, TBC_P5);
		var periodicComponent = effect.getPeriodicComponent();
		var tickScheme = new TickScheme(List.of(0.5, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.5, 1.5, 1.5, 1.5));

		assertThat(effect.getName()).isEqualTo("Curse of Agony");
		assertThat(periodicComponent.tickScheme()).isEqualTo(tickScheme);
	}

	@Test
	void abilityEffectModifier() {
		var effect = getEffect(18288, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Amplify Curse");

		assertModifier(effect, List.of(
				Attribute.of(EFFECT_PCT, 50, ConditionOperator.comma(
						AttributeCondition.of(CURSE_OF_DOOM),
						AttributeCondition.of(CURSE_OF_AGONY)
				)),
				Attribute.of(EFFECT_PCT, 20, AttributeCondition.of(CURSE_OF_EXHAUSTION))
		));
	}

	@Test
	void abilityEffectAbsorption() {
		var effect = getEffect(28610, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Shadow Ward");

		var absorptionComponent = effect.getAbsorptionComponent();

		assertCoefficient(absorptionComponent.coefficient(), 30, SHADOW);
		assertThat(absorptionComponent.condition()).isEqualTo(AttributeCondition.of(SHADOW));
		assertThat(absorptionComponent.min()).isEqualTo(875);
		assertThat(absorptionComponent.max()).isEqualTo(875);
	}

	@Test
	void abilityEffectConversion() {
		var effect = getEffect(15286, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Vampiric Embrace");

		var conversion = effect.getConversion();

		assertThat(conversion.condition()).isEqualTo(AttributeCondition.of(SHADOW));
		assertThat(conversion.from()).isEqualTo(DAMAGE_DONE);
		assertThat(conversion.to()).isEqualTo(PARTY_HEALTH);
		assertThat(conversion.ratioPct()).isEqualTo(Percent.of(15));
	}

	@Test
	void abilityEffectEvent() {
		var effect = getEffect(18288, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Amplify Curse");

		var event = effect.getEvents().get(0);

		assertThat(event.types()).isEqualTo(List.of(SPELL_CAST));
		assertThat(event.condition()).isEqualTo(ConditionOperator.comma(
				AttributeCondition.of(CURSE_OF_DOOM),
				AttributeCondition.of(CURSE_OF_AGONY),
				AttributeCondition.of(CURSE_OF_EXHAUSTION)
		));
		assertThat(event.chance()).isEqualTo(Percent._100);
		assertThat(event.actions()).isEqualTo(List.of(REMOVE_CHARGE));
		assertThat(event.cooldown()).isEqualTo(Duration.ZERO);
		assertThat(event.triggeredSpell()).isNull();
	}

	@Test
	void abilityMultipleLevels() {
		var ability = getClassAbility(TOUCH_OF_WEAKNESS, 7, TBC_P5);

		assertThat(ability.getId()).isEqualTo(25461);
		assertThat(ability.getName()).isEqualTo("Touch of Weakness");

		assertThat(ability.getEffectApplication()).isNotNull();

		var effect = ability.getEffectApplication().effect();

		assertThat(effect.getEffectId()).isEqualTo(25461);
		assertThat(effect.getName()).isEqualTo("Touch of Weakness");

		assertThat(effect.getEvents()).hasSize(1);

		var triggeredSpell = effect.getEvents().get(0).triggeredSpell();

		assertThat(triggeredSpell).isNotNull();
		assertThat(triggeredSpell.getId()).isEqualTo(110025461);
		assertThat(triggeredSpell.getName()).isEqualTo("Touch of Weakness - triggered");

		assertThat(triggeredSpell.getEffectApplication()).isNotNull();

		var triggeredEffect = triggeredSpell.getEffectApplication().effect();

		assertThat(triggeredEffect.getEffectId()).isEqualTo(110025461);
		assertThat(triggeredEffect.getName()).isEqualTo("Touch of Weakness - triggered");
	}

	static void assertCoefficient(Coefficient coefficient, double value, SpellSchool school) {
		assertThat(coefficient.value().value()).isEqualTo(value, PRECISION);
		assertThat(coefficient.school()).isEqualTo(school);
	}

	@Test
	void activatedAbility() {
		var spell = getSpell(132483, TBC_P5);

		assertThat(spell.getName()).isEqualTo("The Skull of Gul'dan");
		assertThat(spell.getTooltip()).isEqualTo("Use: Tap into the power of the skull, increasing spell haste rating by 175 for 20 sec. (2 Min Cooldown)");

		assertEffectApplication(spell, SpellTarget.SELF, 20, 1, 1, 1);

		var effect = spell.getEffectApplication().effect();

		assertThat(effect.getEffectId()).isEqualTo(132483);
		assertModifier(effect, List.of(
			Attribute.of(HASTE_RATING, 175, MiscCondition.SPELL)
		));
	}

	@Test
	void itemProc() {
		var effect = getEffect(127683, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Quagmirran's Eye - proc #1");
		assertThat(effect.getTooltip()).isEqualTo("Equip: Your harmful spells have a chance to increase your spell haste rating by 320 for 6 secs. (Proc chance: 10%, 45s cooldown)");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().get(0);

		assertEvent(
				event,
				List.of(SPELL_HIT),
				MiscCondition.HOSTILE_SPELL,
				10,
				List.of(TRIGGER_SPELL),
				Duration.seconds(45)
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertThat(spell.getId()).isEqualTo(100127683);
		assertThat(spell.getName()).isEqualTo("Quagmirran's Eye - proc #1 - triggered");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTarget.SELF, 6, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(HASTE_RATING, 320, MiscCondition.SPELL)
		));
	}

	@Test
	void setBonusProc1() {
		var effect = getEffect(228963, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Voidheart Raiment - P2 bonus");
		assertThat(effect.getTooltip()).isEqualTo("Your shadow damage spells have a chance to grant you 135 bonus shadow damage for 15 sec. (Proc chance: 5%)");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().get(0);

		assertEvent(
				event,
				List.of(SPELL_HIT),
				AttributeCondition.of(SHADOW),
				5,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertThat(spell.getId()).isEqualTo(100228963);
		assertThat(spell.getName()).isEqualTo("Voidheart Raiment - P2 bonus - triggered");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTarget.SELF, 15, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(POWER, 135, ConditionOperator.and(
						MiscCondition.SPELL_DAMAGE,
						AttributeCondition.of(SHADOW)
				))
		));
	}

	@Test
	void setBonusProc2() {
		var effect = getEffect(1228963, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Voidheart Raiment - P2 bonus");
		assertThat(effect.getTooltip()).isEqualTo("Your fire damage spells have a chance to grant you 135 bonus fire damage for 15 sec. (Proc chance: 5%)");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().get(0);

		assertEvent(
				event,
				List.of(SPELL_HIT),
				AttributeCondition.of(FIRE),
				5,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertThat(spell.getId()).isEqualTo(101228963);
		assertThat(spell.getName()).isEqualTo("Voidheart Raiment - P2 bonus - triggered");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTarget.SELF, 15, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(POWER, 135, ConditionOperator.and(
						MiscCondition.SPELL_DAMAGE,
						AttributeCondition.of(FIRE)
				))
		));
	}

	@Test
	void enchantProc() {
		var effect = getEffect(128003, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Enchant Weapon - Spellsurge - proc #1");
		assertThat(effect.getTooltip()).isEqualTo("Permanently enchant a Melee Weapon to have a 3% chance on spellcast to restore 100 mana to all party members over 10 seconds.");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().get(0);

		assertEvent(
				event,
				List.of(SPELL_CAST),
				AttributeCondition.EMPTY,
				3,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertThat(spell.getId()).isEqualTo(100128003);
		assertThat(spell.getName()).isEqualTo("Enchant Weapon - Spellsurge - proc #1 - triggered");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTarget.PARTY, 10, 1, 1, 1);

		var triggeredEffect = spell.getEffectApplication().effect();

		assertPeriodicComponent(triggeredEffect.getPeriodicComponent(), ComponentType.MANA_GAIN, 0, null, 100, 5, TickScheme.DEFAULT);
		assertThat(triggeredEffect.getTickInterval()).isEqualTo(Duration.seconds(2));
	}

	@Test
	void gemProc() {
		var effect = getEffect(125893, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Mystical Skyfire Diamond - proc #1");
		assertThat(effect.getTooltip()).isEqualTo("Chance to Increase Spell Cast Speed");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().get(0);

		assertEvent(event, List.of(SPELL_CAST), AttributeCondition.EMPTY, 15, List.of(TRIGGER_SPELL), Duration.seconds(35));

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertThat(spell.getId()).isEqualTo(100125893);
		assertThat(spell.getName()).isEqualTo("Mystical Skyfire Diamond - proc #1 - triggered");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTarget.SELF, 6, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(HASTE_RATING, 320, MiscCondition.SPELL)
		));
	}

	@Test
	void talentInfo() {
		var talent = getTalent(WARLOCK, IMPROVED_CORRUPTION, 1, TBC_P5);

		assertThat(talent.getId()).isEqualTo(17810);
		assertThat(talent.getName()).isEqualTo("Improved Corruption");
		assertThat(talent.getRank()).isEqualTo(1);
		assertThat(talent.getMaxRank()).isEqualTo(5);
		assertThat(talent.getTalentCalculatorPosition()).isEqualTo(2);
		assertThat(talent.getTimeRestriction().getUniqueVersion()).isEqualTo(TBC);
		assertThat(talent.getCharacterClass()).isEqualTo(WARLOCK);
		assertThat(talent.getTalentTree()).isEqualTo(AFFLICTION);
		assertThat(talent.getIcon()).isEqualTo("spell_shadow_abominationexplosion");
		assertThat(talent.getTooltip()).isEqualTo("Reduces the casting time of your Corruption spell by 0.4 sec.");
	}

	@ParameterizedTest
	@CsvSource({
			"1, -0.4",
			"2, -0.8",
			"3, -1.2",
			"4, -1.6",
			"5, -2.0"
	})
	void talentModifier(int rank, double value) {
		Talent talent = getTalent(WARLOCK, IMPROVED_CORRUPTION, rank, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(CAST_TIME, value, AttributeCondition.of(CORRUPTION))
		));
	}

	@Test void talentWithTwoAttributeTagets() {
		Talent talent = getTalent(WARLOCK, FEL_INTELLECT, 3, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(PET_INTELLECT_PCT, 15),
				Attribute.of(MAX_MANA_PCT, 3)
		));
	}

	@ParameterizedTest
	@CsvSource({
			"1, VANILLA_P6",
			"2, VANILLA_P6",
			"3, VANILLA_P6",
			"4, VANILLA_P6",
			"5, VANILLA_P6",
			"1, TBC_P5",
			"2, TBC_P5",
			"3, TBC_P5",
			"4, TBC_P5",
			"5, TBC_P5",
	})
	void talentProc(int rank, PhaseId phaseId) {
		var talent = getTalent(WARLOCK, IMPROVED_SHADOW_BOLT, rank, phaseId);

		assertThat(talent.getTooltip()).isEqualTo(
				"Your Shadow Bolt critical strikes increase Shadow damage dealt to the target by " + (rank * 4) + "% until 4 non-periodic damage sources are applied. Effect lasts a maximum of 12 sec."
		);

		var effect = talent.getEffect();

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().get(0);

		assertEvent(
				event,
				List.of(SPELL_CRIT),
				AttributeCondition.of(SHADOW_BOLT),
				100,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertEffectApplication(spell, SpellTarget.TARGET, 12, 4, rank, 5);

		var appliedEffect = spell.getEffectApplication().effect();

		assertModifier(appliedEffect, List.of(
				Attribute.of(DAMAGE_PCT, 4, AttributeCondition.of(SHADOW))
		));
	}

	@Test
	void talentEffectWithMultipleAttributes() {
		var talent = getTalent(WARLOCK, BANE, 5, TBC_P5);

		assertModifier(talent.getEffect(), List.of(
				Attribute.of(CAST_TIME, -0.5, ConditionOperator.comma(
						AttributeCondition.of(SHADOW_BOLT),
						AttributeCondition.of(IMMOLATE)
				)),
				Attribute.of(CAST_TIME, -2, AttributeCondition.of(SOUL_FIRE))
		));
	}

	@Test
	void talentWithConversion() {
		var talent = getTalent(PRIEST, IMPROVED_VAMPIRIC_EMBRACE, 2, TBC_P5);
		var effect = talent.getEffect();

		assertThat(effect.getAugmentedAbility()).isEqualTo(AbilityId.VAMPIRIC_EMBRACE);
		assertConversion(effect.getConversion(), AttributeCondition.of(SHADOW), DAMAGE_DONE, PARTY_HEALTH, 10);
	}

	@Test
	void talentWithStatConversion() {
		var talent = getTalent(PRIEST, IMPROVED_DIVINE_SPIRIT, 2, TBC_P5);
		var effect = talent.getEffect();

		assertThat(effect.getAugmentedAbility()).isEqualTo(AbilityId.DIVINE_SPIRIT);
		assertStatConversion(effect, 0, SPIRIT, POWER, 10, MiscCondition.SPELL);
	}

	@Test
	void talentWithDoubleConversion() {
		var talent = getTalent(WARLOCK, DEMONIC_KNOWLEDGE, 3, TBC_P5);
		var effect = talent.getEffect();

		assertStatConversion(effect, 0, PET_STAMINA, POWER, 12, MiscCondition.SPELL_DAMAGE);
		assertStatConversion(effect, 1, PET_INTELLECT, POWER, 12, MiscCondition.SPELL_DAMAGE);
	}

	@Test
	void talentWithEffectIncrease() {
		var talent = getTalent(WARLOCK, SOUL_SIPHON, 2, TBC_P5);
		var effect = talent.getEffect();
		var effectIncrease = effect.getEffectIncreasePerEffectOnTarget();

		assertThat(effectIncrease).isNotNull();
		assertThat(effectIncrease.condition()).isEqualTo(AttributeCondition.of(AFFLICTION));
		assertThat(effectIncrease.value()).isEqualTo(Percent.of(4));
		assertThat(effectIncrease.max()).isEqualTo(Percent.of(60));
	}

	@Test
	void racialAbility() {
		var ability = (RacialAbility) getSpell(33697, TBC_P5);

		assertThat(ability.getName()).isEqualTo("Blood Fury");
		assertThat(ability.getRank()).isZero();
		assertThat(ability.getTooltip()).isEqualTo("Increases melee attack power by 6 and your damage and healing from spells and effects by up to 5, but reduces healing effects on you by 50%.  Lasts 15 sec.");
		assertThat(ability.getCharacterRestriction().raceIds()).isEqualTo(List.of(ORC));
		assertThat(ability.getCharacterRestriction().characterClassIds()).isEqualTo(List.of(SHAMAN));

		assertThat(ability.getCost()).isEqualTo(new Cost(ResourceType.MANA, 0));
		assertThat(ability.getCastInfo()).isEqualTo(new CastInfo(Duration.ZERO, false, true));
		assertThat(ability.getCooldown()).isEqualTo(Duration.minutes(2));

		assertEffectApplication(ability, SpellTarget.SELF, 15, 1, 1, 1);

		var effect = ability.getEffectApplication().effect();

		assertModifier(effect, List.of(
				Attribute.of(POWER, 3, MiscCondition.SPELL),
				Attribute.of(POWER, 2, MiscCondition.SPELL, true),
				Attribute.of(POWER, 2, MiscCondition.PHYSICAL),
				Attribute.of(POWER, 4, MiscCondition.PHYSICAL, true),
				Attribute.of(HEALING_TAKEN_PCT, -50)
		));
	}

	@Test
	void racialEffect() {
		var effect = getEffect(20555, TBC_P5);

		// todo sprawdzic wymagany race i type == RACIAL_PASSIVE!!!
		// albo obudowac to w race!!!!

		assertModifier(effect, List.of(
				Attribute.of(HEALTH_REGEN_PCT, 10),
				Attribute.of(IN_COMBAT_HEALTH_REGEN_PCT, 10)
		));
	}

	@Test
	void buffIsCorrect() {
		var buff = spellRepository.getBuff(BuffId.CURSE_OF_THE_ELEMENTS, 4, PhaseId.TBC_P5).orElseThrow();

		assertThat(buff.getBuffId()).isEqualTo(BuffId.CURSE_OF_THE_ELEMENTS);
		assertThat(buff.getRank()).isEqualTo(4);
		assertThat(buff.getName()).isEqualTo("Curse of the Elements");
		assertThat(buff.getRequiredLevel()).isNull();
		assertThat(buff.getType()).isEqualTo(BuffType.DEBUFF);
		assertThat(buff.getExclusionGroup()).isEqualTo(BuffExclusionGroup.COE);
		assertThat(buff.getSourceSpell()).isNull();
		assertThat(buff.getPveRoles()).hasSameElementsAs(Set.of(CASTER_DPS));
		assertThat(buff.getCategories()).hasSameElementsAs(Set.of(RAID_BUFF));

		assertModifier(buff.getEffect(), List.of(
				Attribute.of(DAMAGE_TAKEN_PCT, 10, ConditionOperator.comma(
						AttributeCondition.of(SHADOW),
						AttributeCondition.of(FIRE),
						AttributeCondition.of(FROST),
						AttributeCondition.of(ARCANE)
				))
		));
	}

	@Test
	void bufByNamefIsCorrect() {
		var buffNames = spellRepository.getAvailableBuffs(PhaseId.TBC_P5).stream()
				.map(buff -> buff.getName() + "#" + buff.getRank())
				.toList();

		assertThat(buffNames).hasSameElementsAs(List.of(
				"Arcane Brilliance#1",
				"Arcane Brilliance#2",
				"Prayer of Fortitude#2",
				"Prayer of Fortitude#3",
				"Prayer of Spirit#1",
				"Prayer of Spirit#2",
				"Gift of the Wild#2",
				"Gift of the Wild#3",
				"Greater Blessing of Kings#0",
				"Demon Armor#5",
				"Demon Armor#6",
				"Fel Armor#1",
				"Fel Armor#2",
				"Fel Armor (improved)#2",
				"Touch of Shadow#0",
				"Burning Wish#0",
				"Shadowform#0",
				"Brilliant Wizard Oil#0",
				"Superior Wizard Oil#0",
				"Runn Tum Tuber#0",
				"Well Fed (sp)#0",
				"Flask of Supreme Power#0",
				"Flask of Pure Death#0",
				"Spirit of Zanza#0",
				"Greater Arcane Elixir#0",
				"Elixir of Shadow Power#0",
				"Moonkin Aura#0",
				"Wrath of Air Totem#1",
				"Totem of Wrath#1",
				"Drums of Battle#0",
				"Destruction#0",
				"Misery#0",
				"Rallying Cry of the Dragonslayer#0",
				"Spirit of Zandalar#0",
				"Warchief's Blessing#0",
				"Sayge's Dark Fortune of Damage#0",
				"Mol'dar's Moxie#0",
				"Slip'kik's Savvy#0",
				"Songflower Serenade#0",
				"Shadow Weaving#0",
				"Improved Scorch#0",
				"Curse of the Elements#3",
				"Curse of the Elements#4",
				"Curse of the Elements (improved)#3",
				"Curse of the Elements (improved)#4"
		));
	}

	@Nested
	class BuffAttributes {
		@ParameterizedTest(name = "{0}")
		@CsvSource({
				"VANILLA_P1, 1, 31",
				"TBC_P1, 2, 40",
		})
		void talent(PhaseId phaseId, int rank, int statValue) {
			var buff = spellRepository.getBuff(BuffId.ARCANE_BRILLIANCE, rank, phaseId).orElseThrow();

			assertModifier(buff.getEffect(), List.of(
					Attribute.of(INTELLECT, statValue)
			));
		}
	}

	private ClassAbility getClassAbility(AbilityId abilityId, int rank, PhaseId phaseId) {
		return (ClassAbility) spellRepository.getAbility(abilityId, rank, phaseId).orElseThrow();
	}

	private Spell getSpell(int spellId, PhaseId phaseId) {
		return spellRepository.getSpell(spellId, phaseId).orElseThrow();
	}

	private Effect getEffect(int effectId, PhaseId phaseId) {
		return spellRepository.getEffect(effectId, phaseId).orElseThrow();
	}

	private Talent getTalent(CharacterClassId classId, TalentId talentId, int rank, PhaseId phaseId) {
		return spellRepository.getTalent(classId, talentId, rank, phaseId).orElseThrow();
	}
}
