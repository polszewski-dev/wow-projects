package wow.commons.repository.spell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.constant.AbilityIds;
import wow.commons.constant.AttributeConditions;
import wow.commons.constant.EventConditions;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.Described;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectExclusionGroup;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.EffectScope;
import wow.commons.model.effect.component.*;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.spell.component.DirectComponentBonus;
import wow.commons.model.talent.TalentTree;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.config.TalentRestriction.TalentIdRestriction;
import static wow.commons.model.effect.component.EventAction.REMOVE_CHARGE;
import static wow.commons.model.effect.component.EventAction.TRIGGER_SPELL;
import static wow.commons.model.effect.component.EventType.SPELL_CAST;
import static wow.commons.model.effect.component.EventType.SPELL_HIT;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.spell.SpellSchool.*;
import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.SpellTargetType.TARGET;
import static wow.commons.model.spell.component.ComponentCommand.*;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class SpellRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	SpellRepository spellRepository;

	@ParameterizedTest
	@CsvSource({
			"27209, Shadow Bolt",
			"25389, Power Word: Fortitude"
	})
	void name_is_correct(int spellId, String expected) {
		var spell = getSpell(spellId, TBC_P5);

		var actual = spell.getName();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"27209, 11",
			"25389, 7",
			"18288, 0"
	})
	void rank_is_correct(int spellId, int expected) {
		var spell = (ClassAbility) getSpell(spellId, TBC_P5);

		var actual = spell.getRank();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt, 11, Destruction",
			"Fireball, 14, Fire Tree",
	})
	void tree_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getTalentTree();
		var expected = TalentTree.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt, 11, Warlock",
			"Fireball, 14, Mage",
	})
	void required_class_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getRequiredCharacterClassIds();
		var expected = List.of(CharacterClassId.parse(expectedStr));

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void required_classes_are_correct() {
		var racial = (RacialAbility) getSpell(20554, TBC_P5);

		var actual = racial.getRequiredCharacterClassIds();
		var expected = List.of(MAGE, PRIEST, SHAMAN, HUNTER);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadow Bolt, 1, 1",
			"Shadow Bolt, 11, 69",
	})
	void required_level_is_correct(String name, int rank, int expected) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getRequiredLevel();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Devouring Plague, 7, Undead",
			"Touch of Weakness, 7, Blood Elf+Undead",
	})
	void required_race_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getRequiredRaceIds();
		var expected = toList(expectedStr, RaceId::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Soul Link, 0, Imp+Voidwalker+Succubus+Incubus+Felhunter+Felguard+Enslaved",
			"Shadow Bolt, 11, ",
	})
	void required_pet_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getCharacterRestriction().activePet();
		var expected = toList(expectedStr, PetType::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Vampiric Touch, 1, Vampiric Touch",
			"Silence, 0, Silence",
			"Mind Blast, 11, "
	})
	void required_talent_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getCharacterRestriction().talentRestriction();
		var expected = expectedStr != null ? new TalentIdRestriction(expectedStr) : null;

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Corruption, 7, VANILLA_P6, VANILLA_P5",
			"Corruption, 7, TBC_P5, TBC_P0",
	})
	void required_phase_is_correct(String name, int rank, String phaseIdStr, String expectedStr) {
		var ability = getClassAbility(name, rank, PhaseId.parse(phaseIdStr));

		var actual = ability.getEarliestPhaseId();
		var expected = PhaseId.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"27209, CLASS_ABILITY",
			"20554, RACIAL_ABILITY",
			"129132, ACTIVATED_ABILITY",
			"33297, TRIGGERED_SPELL",
	})
	void type_is_correct(int spellId, SpellType expected) {
		var ability = getSpell(spellId, TBC_P5);

		var actual = ability.getType();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Curse of Agony, 1, Curses",
			"Curse of Doom, 1, Curses",
	})
	void category_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getCategory();
		var expected = AbilityCategory.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getCostData")
	void cost_is_correct(CostData data) {
		var name = data.abilityName;
		var rank = data.rank;
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getCost();
		var expected = data.cost;

		assertThat(actual).isEqualTo(expected);
	}

	record CostData(String abilityName, int rank, Cost cost) {}

	static List<CostData> getCostData() {
		return List.of(
			new CostData(SHADOW_BOLT, 11, new Cost(MANA, 420)),
			new CostData(LIFE_TAP, 7, new Cost(HEALTH, 582, Percent.ZERO, coefficient(80, null), null)),
			new CostData(FEAR, 3, new Cost(MANA, 0, Percent.of(12), Coefficient.NONE, null)),
			new CostData(SHADOWBURN, 8, new Cost(MANA, 515, Percent.ZERO, Coefficient.NONE, Reagent.SOUL_SHARD))
		);
	}

	@ParameterizedTest
	@MethodSource("getCastTimeData")
	void cast_time_is_correct(CastTimeData data) {
		var name = data.abilityName;
		var rank = data.rank;
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getCastInfo();
		var expected = data.castInfo;

		assertThat(actual).isEqualTo(expected);
	}

	record CastTimeData(String abilityName, int rank, CastInfo castInfo) {}

	static List<CastTimeData> getCastTimeData() {
		return List.of(
				new CastTimeData(SHADOW_BOLT, 11, new CastInfo(Duration.seconds(3), false, false)),
				new CastTimeData(LIFE_TAP, 7, new CastInfo(Duration.ZERO, false, false)),
				new CastTimeData(AMPLIFY_CURSE, 0, new CastInfo(Duration.ZERO, false, true)),
				new CastTimeData(MIND_FLAY, 7, new CastInfo(Duration.ZERO, true, false))
		);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadowburn, 7, 15",
			"Incinerate, 2, 0",
	})
	void cooldown_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getCooldown();
		var expected = Duration.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadowburn, 7, 20",
			"Incinerate, 2, 30",
	})
	void range_is_correct(String name, int rank, int expected) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getRange();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Shadowburn, 7, false",
			"Incinerate, 2, true",
	})
	void bolt_is_correct(String name, int rank, boolean expected) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.isBolt();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Conflagrate, 6, Immolate",
			"Incinerate, 2, ",
	})
	void effect_removed_on_hit_is_correct(String name, int rank, String expectedStr) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getEffectRemovedOnHit();
		var expected = expectedStr != null ? AbilityId.of(expectedStr) : null;

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getDirectComponentData")
	void direct_component_is_correct(DirectComponentData data) {
		var name = data.abilityName;
		var rank = data.rank;
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getDirectComponent();
		var expected = data.directComponent;

		assertThat(actual).isEqualTo(expected);
	}

	record DirectComponentData(String abilityName, int rank, DirectComponent directComponent) {}

	static List<DirectComponentData> getDirectComponentData() {
		return List.of(
				new DirectComponentData(SHADOW_BOLT, 11, new DirectComponent(
						List.of(
								new DealDamageDirectly(
										SpellTargets.ENEMY,
										SpellTargetCondition.EMPTY,
										new Coefficient(new Percent(85.71), SHADOW),
										544,
										607,
										null
								)
						)
				)),
				new DirectComponentData(INCINERATE, 2, new DirectComponent(
						List.of(
								new DealDamageDirectly(
										SpellTargets.ENEMY,
										SpellTargetCondition.EMPTY,
										coefficient(71.43, FIRE),
										444,
										514,
										new DirectComponentBonus(111, 128, AbilityIds.IMMOLATE)
								)
						)
				)),
				new DirectComponentData(LIFE_TAP, 7, new DirectComponent(
						List.of(
								new Copy(
										SpellTargets.SELF,
										SpellTargetCondition.EMPTY,
										null,
										From.HEALTH_PAID,
										To.MANA_GAIN,
										Percent._100
								)
						)
				)),
				new DirectComponentData(HOLY_SHOCK, 5, new DirectComponent(
						List.of(
								new DealDamageDirectly(
										SpellTargets.ANY,
										SpellTargetCondition.HOSTILE,
										coefficient(42.85, HOLY),
										721,
										779,
										null
								),
								new HealDirectly(
										SpellTargets.ANY,
										SpellTargetCondition.FRIENDLY,
										coefficient(42.85, HOLY),
										913,
										987,
										null
								)
						)
				))
		);
	}

	@ParameterizedTest
	@MethodSource("getEffectApplicationData")
	void effect_application_is_correct(EffectApplicationData data) {
		var name = data.abilityName;
		var rank = data.rank;
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getEffectApplication();
		var expected = data.getEffectApplication(spellRepository);

		assertThat(actual).isEqualTo(expected);
	}

	record EffectApplicationData(String abilityName, int rank, Integer effectId, EffectApplication effectApplication) {
		EffectApplication getEffectApplication(SpellRepository spellRepository) {
			if (effectApplication == null) {
				return null;
			}

			var commands = effectApplication.commands().stream()
					.map(command -> swapEffect(command, spellRepository))
					.toList();

			return new EffectApplication(commands);
		}

		private ApplyEffect swapEffect(ApplyEffect command, SpellRepository spellRepository) {
			return new ApplyEffect(
					command.target(),
					command.condition(),
					spellRepository.getEffect(EffectId.of(effectId), TBC_P5).orElseThrow(),
					command.duration(),
					command.numStacks(),
					command.numCharges(),
					command.replacementMode()
			);
		}
	}

	static List<EffectApplicationData> getEffectApplicationData() {
		return List.of(
				new EffectApplicationData(CURSE_OF_AGONY, 7, 27218, new EffectApplication(
						List.of(
								new ApplyEffect(
										SpellTargets.ENEMY,
										SpellTargetCondition.EMPTY,
										Effect.EMPTY, // can't get correct effect within static method
										Duration.seconds(24),
										1,
										1,
										EffectReplacementMode.DEFAULT
								)
						)
				)),
				new EffectApplicationData(LIFE_TAP, 7, null, null)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"Incinerate, 2, Deals 444 to 514 Fire damage to your target and an additional 111 to 128 Fire damage if the target is affected by an Immolate spell.",
	})
	void tooltip_is_correct(String name, int rank, String expected) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getTooltip();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Incinerate, 2, spell_fire_burnout",
	})
	void icon_is_correct(String name, int rank, String expected) {
		var ability = getClassAbility(name, rank, TBC_P5);

		var actual = ability.getIcon();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"25311, Corruption",
			"25389, Power Word: Fortitude"
	})
	void effect_name_is_correct(int effectId, String expected) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getName();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"25311, VANILLA_P6, VANILLA_P5",
			"25311, TBC_P5, TBC_P0",
	})
	void required_phase_is_correct(int effectId, String phaseIdStr, String expectedStr) {
		var effect = getEffect(effectId, PhaseId.parse(phaseIdStr));

		var actual = effect.getEarliestPhaseId();
		var expected = PhaseId.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"25311, 1",
			"11129, 10",
	})
	void effect_max_stacks_is_correct(int effectId, int expected) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getMaxStacks();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"25311, Personal",
			"25389, Global"
	})
	void effect_scope_is_correct(int effectId, String expectedStr) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getScope();
		var expected = EffectScope.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"27218, Curse",
			"28189, Armor"
	})
	void effect_exclusion_group_is_correct(int effectId, String expectedStr) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getExclusionGroup();
		var expected = EffectExclusionGroup.parse(expectedStr);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"1230212, Corruption+Immolate",
			"1223065, Lightning Shield"
	})
	void effect_augmented_abilities_are_correct(int effectId, String expectedStr) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getAugmentedAbilities();
		var expected = toList(expectedStr, AbilityId::parse);

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"25311, spell_shadow_abominationexplosion",
	})
	void effect_icon_is_correct(int effectId, String expected) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getIcon();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource(delimiter = ';', value = {
			"25311; Corrupts the target, causing 822 Shadow damage over 18 sec.",
	})
	void effect_tooltip_is_correct(int effectId, String expected) {
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getTooltip();

		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getPeriodicComponentData")
	void periodic_component_is_correct(PeriodicComponentData data) {
		var effectId = data.effectId;
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getPeriodicComponent();
		var expected = data.periodicComponent;

		assertThat(actual).isEqualTo(expected);
	}

	record PeriodicComponentData(int effectId, PeriodicComponent periodicComponent) {}

	static List<PeriodicComponentData> getPeriodicComponentData() {
		return List.of(
				new PeriodicComponentData(
						27218,
						new PeriodicComponent(
								List.of(
										new DealDamagePeriodically(
												SpellTargets.TARGET,
												coefficient(120, SHADOW),
												1356,
												12,
												new TickScheme(List.of(0.5, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.5, 1.5, 1.5, 1.5))
										)
								),
								Duration.seconds(2)
						)
				),
				new PeriodicComponentData(
						30908,
						new PeriodicComponent(
								List.of(
										new LoseManaPeriodically(
												SpellTargets.TARGET,
												coefficient(0, SHADOW),
												1000,
												5
										),
										new Copy(
												SpellTargets.SELF,
												SpellTargetCondition.EMPTY,
												null,
												From.MANA_LOSS,
												To.MANA_GAIN,
												Percent._100
										)
								),
								Duration.seconds(1)
						)
				)
		);
	}

	@ParameterizedTest
	@MethodSource("getModifierComponentData")
	void modifier_component_is_correct(ModifierComponentData data) {
		var effectId = data.effectId;
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getModifierComponent();
		var expected = data.modifierComponent;

		assertThat(actual).isEqualTo(expected);
	}

	record ModifierComponentData(int effectId, ModifierComponent modifierComponent) {}

	static List<ModifierComponentData> getModifierComponentData() {
		return List.of(
				new ModifierComponentData(
						18288,
						new ModifierComponent(
								Attributes.of(
										Attribute.of(EFFECT_PCT, 50, AttributeCondition.comma(
												AttributeConditions.CURSE_OF_DOOM,
												AttributeConditions.CURSE_OF_AGONY
										)),
										Attribute.of(EFFECT_PCT, 20, AttributeConditions.CURSE_OF_EXHAUSTION)
								)
						)
				),
				new ModifierComponentData(
						18803,
						new ModifierComponent(
								Attributes.of(
										Attribute.of(HASTE_RATING, 320, AttributeConditions.SPELL)
								)
						)
				)
		);
	}

	@ParameterizedTest
	@MethodSource("getAbsorptionComponentData")
	void absorption_component_is_correct(AbsorptionComponentData data) {
		var effectId = data.effectId;
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getAbsorptionComponent();
		var expected = data.absorptionComponent;

		assertThat(actual).isEqualTo(expected);
	}

	record AbsorptionComponentData(int effectId, AbsorptionComponent absorptionComponent) {}

	static List<AbsorptionComponentData> getAbsorptionComponentData() {
		return List.of(
				new AbsorptionComponentData(
						28610,
						new AbsorptionComponent(
								coefficient(30, SHADOW),
								AbsorptionCondition.of(SHADOW),
								875,
								875
						)
				)
		);
	}

	@ParameterizedTest
	@MethodSource("getEventData")
	void events_are_correct(EventData data) {
		var effectId = data.effectId;
		var effect = getEffect(effectId, TBC_P5);

		var actual = effect.getEvents();
		var expected = data.getEvents(spellRepository);

		assertThat(actual).isEqualTo(expected);
	}

	record EventData(int effectId, List<Integer> triggeredSpellIds, List<Event> events) {
		List<Event> getEvents(SpellRepository spellRepository) {
			return IntStream.range(0, events.size())
					.mapToObj(i -> swapTriggeredSpell(i, spellRepository))
					.toList();
		}

		private Event swapTriggeredSpell(int i, SpellRepository spellRepository) {
			var event = events.get(i);
			var spellId = SpellId.ofNullable(triggeredSpellIds.get(i));

			return new Event(
					event.types(),
					event.condition(),
					event.chance(),
					event.actions(),
					spellId != null ? spellRepository.getSpell(spellId, TBC_P5).orElseThrow() : null,
					event.actionParameters()
			);
		}
	}

	static List<EventData> getEventData() {
		return List.of(
				new EventData(
						18288,
						Collections.singletonList(null),
						List.of(
								new Event(
										List.of(SPELL_CAST),
										EventCondition.comma(
												EventConditions.CURSE_OF_DOOM,
												EventConditions.CURSE_OF_AGONY,
												EventConditions.CURSE_OF_EXHAUSTION
										),
										Percent._100,
										List.of(REMOVE_CHARGE),
										null,
										EventActionParameters.EMPTY
								)
						)
				),
				new EventData(
						228963,
						List.of(37377),
						List.of(
								new Event(
										List.of(SPELL_HIT),
										EventConditions.SHADOW,
										Percent.of(5),
										List.of(TRIGGER_SPELL),
										null,
										EventActionParameters.EMPTY
								)
						)
				)
		);
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

	@Test
	void all_spells_are_valid() {
		var spells = spellRepository.getAllSpells();

		for (var spell : spells) {
			var validationError = validateSpell(spell);

			assertThat(validationError)
					.withFailMessage(validationError != null ? validationError : "")
					.isNull();
		}
	}

	@Test
	void all_effects_are_valid() {
		var effects = spellRepository.getAllEffects();

		for (var effect : effects) {
			var validationError = validateEffect(effect);

			assertThat(validationError)
					.withFailMessage(validationError != null ? validationError : "")
					.isNull();
		}
	}

	private String validateSpell(Spell spell) {
		if (!(spell instanceof Ability ability)) {
			return null;
		}

		if (ability.isChanneled() && !ability.getCastTime().isZero()) {
			return "Channeled ability with non-zero cast time: " + ability;
		}

		for (var applyEffectCommand : ability.getApplyEffectCommands()) {
			var effectTarget = applyEffectCommand.target();
			var effect = applyEffectCommand.effect();

			if (ability.isChanneled() && effectTarget.isAoE() && !effectTarget.hasType(GROUND)) {
				return "Channeled ability with AoE effect target: " + ability;
			}

			for (var periodicCommand : effect.getPeriodicCommands()) {
				var commandTarget = periodicCommand.target();

				if (effectTarget.hasType(GROUND) && commandTarget.hasType(TARGET)) {
					return "Ground effect has no target specified";
				}
			}
		}

		return null;
	}

	private String validateEffect(Effect effect) {
		if (effect.hasAugmentedAbilities() &&
				(effect.hasPeriodicComponent() || effect.hasAbsorptionComponent())) {
			return "Can't have augmented ability with either periodic or absorption component: " + effect;
		}

		if (effect.hasPeriodicComponent()) {
			if (effect.getTickInterval() == null) {
				return "No tick interval: " + effect;
			}
		}

		return null;
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

	private <T> List<T> toList(String value, Function<String, T> mapper) {
		return value != null
				? Stream.of(value.split("\\+")).map(String::trim).map(mapper).toList()
				: List.of();
	}
}
