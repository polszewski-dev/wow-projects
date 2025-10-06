package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.constant.AbilityIds;
import wow.commons.constant.EventConditions;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.DruidFormType;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.talent.TalentTree;
import wow.test.commons.AbilityNames;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.*;
import static wow.commons.model.character.CreatureType.DEMON;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.effect.component.EventCondition.*;
import static wow.commons.model.spell.SpellSchool.*;
import static wow.commons.util.condition.EventConditionParser.parseCondition;
import static wow.test.commons.AbilityNames.AIMED_SHOT;
import static wow.test.commons.AbilityNames.MULTI_SHOT;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
class EventConditionParserTest {
	@ParameterizedTest
	@MethodSource("getTestData")
	void testValues(TestData data) {
		var parsed = parseCondition(data.string);

		assertThat(parsed).isEqualTo(data.condition);
	}

	static List<TestData> getTestData() {
		return TEST_DATA;
	}

	static final List<TestData> TEST_DATA = List.of(
			testData(
					null,
					EMPTY
			),
			testData(
					"",
					EMPTY
			),
			testData(
					"Spell",
					of(ActionType.SPELL)
			),
			testData(
					"SpellDamage",
					of(PowerType.SPELL_DAMAGE)
			),
			testData(
					"ShadowTree",
					of(TalentTree.SHADOW)
			),
			testData(
					"Shadow",
					of(SHADOW)
			),
			testData(
					"Shadow Bolt",
					of(AbilityIds.SHADOW_BOLT)
			),
			testData(
					"Curses",
					of(AbilityCategory.CURSES)
			),
			testData(
					"Undead",
					of(UNDEAD)
			),
			testData(
					"CatForm",
					of(DruidFormType.CAT_FORM)
			),
			testData(
					"OwnerHas:Mana Shield",
					new OwnerHasEffectCondition(MANA_SHIELD)
			),
			testData(
					"OwnerIsChanneling:Drain Soul",
					new OwnerIsChannelingCondition(DRAIN_SOUL)
			),
			testData(
					"TargetClass=Warlock",
					new TargetClassCondition(CharacterClassId.WARLOCK)
			),
			testData(
					"Direct",
					new IsDirect()
			),
			testData(
					"Periodic",
					new IsPeriodic()
			),
			testData(
					"HostileSpell",
					new IsHostileSpell()
			),
			testData(
					"NormalMeleeAttack",
					new IsNormalMeleeAttack()
			),
			testData(
					"SpecialAttack",
					new IsSpecialAttack()
			),
			testData(
					"HasManaCost",
					new HasManaCost()
			),
			testData(
					"HasCastTime",
					new HasCastTime()
			),
			testData(
					"HasCastTimeUnder10Sec",
					new HasCastTimeUnder10Sec()
			),
			testData(
					"CanCrit",
					new CanCrit()
			),
			testData(
					"HadNoCrit",
					new HadNoCrit()
			),
			testData(
					"TargetingOthers",
					new IsTargetingOthers()
			),
			testData(
					"OwnerHealthBelow35%",
					new OwnerHealthBelowPct(35)
			),
			testData(
					"TargetHealthBelow20%",
					new TargetHealthBelowPct(20)
			),
			testData(
					"Create Firestone (Greater)",
					of(CREATE_FIRESTONE_GREATER)
			),
			testData(
					"(Create Firestone (Greater))",
					of(CREATE_FIRESTONE_GREATER)
			),
			testData(
					"((Create Firestone (Greater)))",
					of(CREATE_FIRESTONE_GREATER)
			),
			testData(
					"Power Word: Shield",
					of(AbilityId.of(AbilityNames.POWER_WORD_SHIELD))
			),
			testData(
					"Shadow Bolt | Incinerate",
					or(of(SHADOW_BOLT), of(INCINERATE))
			),
			testData(
					"Shadow Bolt & Incinerate",
					and(of(SHADOW_BOLT), of(INCINERATE))
			),
			testData(
					"Shadow Bolt, Incinerate",
					comma(of(SHADOW_BOLT), of(INCINERATE))
			),
			testData(
					"Healing & Direct",
					and(EventConditions.HEALING, EventCondition.IS_DIRECT)
			),
			testData(
					"Shadow | Frost",
					or(EventConditions.SHADOW, EventConditions.FROST)
			),
			testData(
					"Shadow | Frost | Fire",
					or(of(SHADOW), EventCondition.or(of(FROST), of(FIRE)))
			),
			testData(
					"Physical & OwnerHealthBelow40%",
					and(EventConditions.PHYSICAL, new OwnerHealthBelowPct(40))
			),
			testData(
					"Physical & Undead, Demon",
					and(EventConditions.PHYSICAL, comma(of(UNDEAD), of(DEMON)))
			),
			testData(
					"Direct & Immolate",
					and(new IsDirect(), of(IMMOLATE))
			),
			testData(
					"SpellDamage & Shadow, Fire, Frost",
					and(EventConditions.SPELL_DAMAGE, comma(of(SHADOW), of(FIRE), of(FROST)))
			),
			testData(
					"Multi-Shot, Aimed Shot",
					comma(of(AbilityId.of(MULTI_SHOT)), of(AbilityId.of(AIMED_SHOT)))
			)
	);

	record TestData(String string, EventCondition condition) {}

	static TestData testData(String string, EventCondition condition) {
		return new TestData(string, condition);
	}
}