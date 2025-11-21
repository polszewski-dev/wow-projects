package wow.commons.repository.spell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.constant.AbilityIds;
import wow.commons.constant.AttributeConditions;
import wow.commons.constant.EventConditions;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.Described;
import wow.commons.model.config.TalentRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.component.AbsorptionCondition;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.*;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.attribute.AttributeScaling.LevelScalingByFactor;
import static wow.commons.model.character.CharacterClassId.SHAMAN;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.effect.component.EventAction.REMOVE_CHARGE;
import static wow.commons.model.effect.component.EventAction.TRIGGER_SPELL;
import static wow.commons.model.effect.component.EventType.SPELL_CAST;
import static wow.commons.model.effect.component.EventType.SPELL_HIT;
import static wow.commons.model.pve.PhaseId.*;
import static wow.commons.model.spell.SpellSchool.SHADOW;
import static wow.commons.model.talent.TalentTree.DESTRUCTION;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TestConstants.PRECISION;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class SpellRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	SpellRepository spellRepository;

	@Test
	void abilityInfo() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);

		assertThat(ability.getNameRank()).isEqualTo(new AbilityNameRank(SHADOW_BOLT, 11));
		assertThat(ability.getAbilityId()).isEqualTo(AbilityIds.SHADOW_BOLT);
		assertThat(ability.getRank()).isEqualTo(11);
		assertThat(ability.getTalentTree()).isEqualTo(ability.getTalentTree()).isEqualTo(DESTRUCTION);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt,      11, WARLOCK, 69,       ,           , ",
			"Shadowburn,        8, WARLOCK, 70,       , Shadowburn, ",
			"Soul Link,         0, WARLOCK,  1,       , Soul Link,  Imp+Voidwalker+Succubus+Incubus+Felhunter+Felguard+Enslaved",
			"Devouring Plague,  7, PRIEST,  68, UNDEAD,           , ",
	})
	void abilityCharacterRestriction(String name, int rank, CharacterClassId characterClassId, int level, RaceId raceId, String talentName, String petTypes) {
		var ability = getClassAbility(name, rank, TBC_P5);
		var characterRestriction = ability.getCharacterRestriction();

		assertThat(characterRestriction.characterClassIds()).isEqualTo(List.of(characterClassId));
		assertThat(characterRestriction.level()).isEqualTo(level);
		assertThat(characterRestriction.raceIds()).isEqualTo(raceId != null ? List.of(raceId) : List.of());
		assertThat(characterRestriction.talentRestriction()).isEqualTo(TalentRestriction.of(talentName));

		var activePets = petTypes != null ? Stream.of(petTypes.split("\\+")).map(PetType::parse).toList() : List.of();

		assertThat(characterRestriction.activePet()).isEqualTo(activePets);
	}

	@Test
	void abilityTimeRestriction() {
		var ability = getClassAbility(SHADOW_BOLT, 11, TBC_P5);
		var timeRestriction = ability.getTimeRestriction();

		assertThat(timeRestriction).isEqualTo(TimeRestriction.of(TBC_P0));
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

		assertId(ability, 27209);
		assertThat(ability.getCooldown()).isEqualTo(Duration.ZERO);
		assertThat(ability.getRange()).isEqualTo(30);
		assertThat(ability.getRequiredEffect()).isNull();
		assertThat(ability.getEffectRemovedOnHit()).isNull();
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt,  11, 3, false, false",
			"Drain Life,    8, 0, true, false",
			"Amplify Curse, 0, 0, false, true",
	})
	void abilityCastInfo(String name, int rank, double castTime, boolean channeled, boolean ignoresGcd) {
		var ability = getClassAbility(name, rank, TBC_P5);
		var castInfo = ability.getCastInfo();

		assertThat(castInfo.castTime()).isEqualTo(Duration.seconds(castTime));
		assertThat(castInfo.channeled()).isEqualTo(channeled);
		assertThat(castInfo.ignoresGcd()).isEqualTo(ignoresGcd);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt,      11, MANA,   420,  0,  0,           ",
			"Life Tap,          7, HEALTH, 582,  0, 80,           ",
			"Summon Voidwalker, 0, MANA,     0, 80,  0, SOUL_SHARD",
	})
	void abilityCost(String name, int rank, ResourceType resourceType, int amount, int baseStatPct, double coeffValue, Reagent reagent) {
		var ability = getClassAbility(name, rank, TBC_P5);
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

		assertThat(directComponent.target()).isEqualTo(SpellTargets.ENEMY);
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
		assertThat(bonus.requiredEffect()).isEqualTo(AbilityIds.IMMOLATE);
	}

	@Test
	void abilityEffectApplication() {
		var ability = getClassAbility(CORRUPTION, 8, TBC_P5);
		var effectApplication = ability.getEffectApplication();

		assertEffectApplication(ability, SpellTargets.ENEMY, 18, 1, 1, 1);

		assertId(effectApplication.effect(), ability.getId().value());
		assertId(effectApplication.effect(), 27216);
	}

	@Test
	void abilitySpell() {
		var spell = getSpell(110025441, TBC_P5);

		assertId(spell, 110025441);
		assertThat(spell.getName()).isEqualTo("Feedback - triggered");

		var directComponent = spell.getDirectComponents().getFirst();

		assertThat(directComponent.target()).isEqualTo(SpellTargets.ATTACKER);
		assertThat(directComponent.type()).isEqualTo(ComponentType.MANA_DRAIN);
		assertCoefficient(directComponent.coefficient(), 0, SHADOW);
		assertThat(directComponent.min()).isEqualTo(165);
		assertThat(directComponent.max()).isEqualTo(165);
		assertThat(directComponent.bolt()).isFalse();
	}

	@Test
	void abilityEffect() {
		var effect = getEffect(25311, VANILLA_P6);

		assertId(effect, 25311);
		assertThat(effect.getName()).isEqualTo("Corruption");
		assertThat(effect.getTimeRestriction()).isEqualTo(TimeRestriction.of(VANILLA_P5));
		assertThat(effect.getMaxStacks()).isEqualTo(1);
		assertThat(effect.getIcon()).isEqualTo("spell_shadow_abominationexplosion");
		assertThat(effect.getTooltip()).isEqualTo("Corrupts the target, causing 822 Shadow damage over 18 sec.");
	}

	@Test
	void abilityEffectPeriodic() {
		var effect = getEffect(25311, VANILLA_P6);

		assertThat(effect.getName()).isEqualTo("Corruption");

		var periodicComponent = effect.getPeriodicComponent();

		assertPeriodicComponent(periodicComponent, ComponentType.DAMAGE, 100, SHADOW, 822, 6, 3, TickScheme.DEFAULT);
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
				Attribute.of(EFFECT_PCT, 50, AttributeCondition.comma(
						AttributeConditions.CURSE_OF_DOOM,
						AttributeConditions.CURSE_OF_AGONY
				)),
				Attribute.of(EFFECT_PCT, 20, AttributeConditions.CURSE_OF_EXHAUSTION)
		));
	}

	@Test
	void abilityEffectAbsorption() {
		var effect = getEffect(28610, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Shadow Ward");

		var absorptionComponent = effect.getAbsorptionComponent();

		assertCoefficient(absorptionComponent.coefficient(), 30, SHADOW);
		assertThat(absorptionComponent.condition()).isEqualTo(AbsorptionCondition.of(SHADOW));
		assertThat(absorptionComponent.min()).isEqualTo(875);
		assertThat(absorptionComponent.max()).isEqualTo(875);
	}

	@Test
	void abilityEffectEvent() {
		var effect = getEffect(18288, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Amplify Curse");

		var event = effect.getEvents().getFirst();

		assertThat(event.types()).isEqualTo(List.of(SPELL_CAST));
		assertThat(event.condition()).isEqualTo(EventCondition.comma(
				EventConditions.CURSE_OF_DOOM,
				EventConditions.CURSE_OF_AGONY,
				EventConditions.CURSE_OF_EXHAUSTION
		));
		assertThat(event.chance()).isEqualTo(Percent._100);
		assertThat(event.actions()).isEqualTo(List.of(REMOVE_CHARGE));
		assertThat(event.triggeredSpell()).isNull();
	}

	@Test
	void abilityMultipleLevels() {
		var ability = getClassAbility(TOUCH_OF_WEAKNESS, 7, TBC_P5);

		assertId(ability, 25461);
		assertThat(ability.getName()).isEqualTo("Touch of Weakness");

		assertThat(ability.getEffectApplication()).isNotNull();

		var effect = ability.getEffectApplication().effect();

		assertId(effect, 25461);
		assertThat(effect.getName()).isEqualTo("Touch of Weakness");

		assertThat(effect.getEvents()).hasSize(1);

		var triggeredSpell = effect.getEvents().getFirst().triggeredSpell();

		assertThat(triggeredSpell).isNotNull();
		assertId(triggeredSpell, 110025461);
		assertThat(triggeredSpell.getName()).isEqualTo("Touch of Weakness - triggered");

		assertThat(triggeredSpell.getEffectApplication()).isNotNull();

		var triggeredEffect = triggeredSpell.getEffectApplication().effect();

		assertId(triggeredEffect, 110025461);
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

		assertEffectApplication(spell, SpellTargets.SELF, 20, 1, 1, 1);

		var effect = spell.getEffectApplication().effect();

		assertId(effect, 132483);
		assertModifier(effect, List.of(
			Attribute.of(HASTE_RATING, 175, AttributeConditions.SPELL)
		));
	}

	@Test
	void itemProc() {
		var effect = getEffect(127683, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Quagmirran's Eye - proc #1");
		assertThat(effect.getTooltip()).isEqualTo("Equip: Your harmful spells have a chance to increase your spell haste rating by 320 for 6 secs. (Proc chance: 10%, 45s cooldown)");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().getFirst();

		assertEvent(
				event,
				List.of(SPELL_HIT),
				EventCondition.IS_HOSTILE_SPELL,
				10,
				List.of(TRIGGER_SPELL),
				Duration.seconds(45)
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertId(spell, 33297);
		assertThat(spell.getName()).isEqualTo("Spell Haste Trinket");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTargets.SELF, 6, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(HASTE_RATING, 320, AttributeConditions.SPELL)
		));
	}

	@Test
	void setBonusProc1() {
		var effect = getEffect(228963, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Voidheart Raiment - P2 bonus");
		assertThat(effect.getTooltip()).isEqualTo("Your shadow damage spells have a chance to grant you 135 bonus shadow damage for 15 sec. (Proc chance: 5%)");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().getFirst();

		assertEvent(
				event,
				List.of(SPELL_HIT),
				EventConditions.SHADOW,
				5,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertId(spell, 37377);
		assertThat(spell.getName()).isEqualTo("Flameshadow");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTargets.SELF, 15, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(POWER, 135, AttributeCondition.and(
						AttributeConditions.SPELL_DAMAGE,
						AttributeConditions.SHADOW
				))
		));
	}

	@Test
	void setBonusProc2() {
		var effect = getEffect(1228963, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Voidheart Raiment - P2 bonus");
		assertThat(effect.getTooltip()).isEqualTo("Your fire damage spells have a chance to grant you 135 bonus fire damage for 15 sec. (Proc chance: 5%)");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().getFirst();

		assertEvent(
				event,
				List.of(SPELL_HIT),
				EventConditions.FIRE,
				5,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertId(spell, 39437);
		assertThat(spell.getName()).isEqualTo("Shadowflame");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTargets.SELF, 15, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(POWER, 135, AttributeCondition.and(
						AttributeConditions.SPELL_DAMAGE,
						AttributeConditions.FIRE
				))
		));
	}

	@Test
	void enchantProc() {
		var effect = getEffect(128003, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Enchant Weapon - Spellsurge - proc #1");
		assertThat(effect.getTooltip()).isEqualTo("Permanently enchant a Melee Weapon to have a 3% chance on spellcast to restore 100 mana to all party members over 10 seconds.");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().getFirst();

		assertEvent(
				event,
				List.of(SPELL_CAST),
				EventCondition.EMPTY,
				3,
				List.of(TRIGGER_SPELL),
				Duration.ZERO
		);

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertId(spell, 100128003);
		assertThat(spell.getName()).isEqualTo("Enchant Weapon - Spellsurge - proc #1 - triggered");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTargets.PARTY, 10, 1, 1, 1);

		var triggeredEffect = spell.getEffectApplication().effect();

		assertPeriodicComponent(triggeredEffect.getPeriodicComponent(), ComponentType.MANA_GAIN, 0, null, 100, 5, 2, TickScheme.DEFAULT);
	}

	@Test
	void gemProc() {
		var effect = getEffect(125893, TBC_P5);

		assertThat(effect.getName()).isEqualTo("Mystical Skyfire Diamond - proc #1");
		assertThat(effect.getTooltip()).isEqualTo("Chance to Increase Spell Cast Speed");

		assertThat(effect.getEvents()).hasSize(1);

		var event = effect.getEvents().getFirst();

		assertEvent(event, List.of(SPELL_CAST), EventCondition.EMPTY, 15, List.of(TRIGGER_SPELL), Duration.seconds(35));

		var spell = event.triggeredSpell();

		assertThat(spell).isNotNull();

		assertId(spell, 32837);
		assertThat(spell.getName()).isEqualTo("Spell Focus Trigger");
		assertThat(spell.getTooltip()).isNull();

		assertEffectApplication(spell, SpellTargets.SELF, 6, 1, 1, 1);
		assertModifier(spell.getEffectApplication().effect(), List.of(
				Attribute.of(HASTE_RATING, 320, AttributeConditions.SPELL)
		));
	}

	@Test
	void racialAbility() {
		var ability = (RacialAbility) getSpell(33697, TBC_P5);

		assertThat(ability.getName()).isEqualTo("Blood Fury");
		assertThat(ability.getRank()).isZero();
		assertThat(ability.getTooltip()).isEqualTo("Increases melee attack power by 6 and your damage and healing from spells and effects by up to 5, but reduces healing effects on you by 50%. Lasts 15 sec.");
		assertThat(ability.getCharacterRestriction().raceIds()).isEqualTo(List.of(ORC));
		assertThat(ability.getCharacterRestriction().characterClassIds()).isEqualTo(List.of(SHAMAN));

		assertThat(ability.getCost()).isEqualTo(new Cost(ResourceType.MANA, 0));
		assertThat(ability.getCastInfo()).isEqualTo(new CastInfo(Duration.ZERO, false, true));
		assertThat(ability.getCooldown()).isEqualTo(Duration.minutes(2));

		assertEffectApplication(ability, SpellTargets.SELF, 15, 1, 1, 1);

		var effect = ability.getEffectApplication().effect();

		assertModifier(effect, List.of(
				Attribute.of(POWER, 3, AttributeConditions.SPELL, new LevelScalingByFactor(2)),
				Attribute.of(POWER, 2, AttributeConditions.PHYSICAL, new LevelScalingByFactor(4)),
				Attribute.of(HEALING_TAKEN_PCT, -50)
		));
	}

	@Test
	void getRacialEffects() {
		var effects = spellRepository.getRacialEffects(ORC, GameVersionId.TBC);
		var effectNames = effects.stream().map(Described::getName).toList();

		assertThat(effectNames).hasSameElementsAs(List.of(
				"Axe Specialization",
				"Command",
				"Hardiness"
		));
	}

	@Test
	void racialEffect() {
		var effect = getEffect(20555, TBC_P5);

		assertModifier(effect, List.of(
				Attribute.of(HEALTH_REGEN_PCT, 10),
				Attribute.of(IN_COMBAT_HEALTH_REGEN_PCT, 10)
		));
	}

	private ClassAbility getClassAbility(String name, int rank, PhaseId phaseId) {
		return (ClassAbility) spellRepository.getAbility(name, rank, phaseId).orElseThrow();
	}

	private Spell getSpell(int spellId, PhaseId phaseId) {
		return spellRepository.getSpell(SpellId.of(spellId), phaseId).orElseThrow();
	}

	private Effect getEffect(int effectId, PhaseId phaseId) {
		return spellRepository.getEffect(EffectId.of(effectId), phaseId).orElseThrow();
	}
}
