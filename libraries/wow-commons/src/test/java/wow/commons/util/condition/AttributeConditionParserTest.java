package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.constant.AbilityIds;
import wow.commons.constant.AttributeConditions;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.MovementType;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.talent.TalentTree;
import wow.test.commons.AbilityNames;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.*;
import static wow.commons.model.attribute.AttributeCondition.*;
import static wow.commons.model.character.CreatureType.DEMON;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.spell.SpellSchool.*;
import static wow.commons.util.condition.AttributeConditionParser.parseCondition;
import static wow.test.commons.AbilityNames.AIMED_SHOT;
import static wow.test.commons.AbilityNames.MULTI_SHOT;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
class AttributeConditionParserTest {
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
					"Shadow Tree",
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
					"Succubus",
					of(PetType.SUCCUBUS)
			),
			testData(
					"Undead",
					of(UNDEAD)
			),
			testData(
					"Skinning",
					of(ProfessionId.SKINNING)
			),
			testData(
					"Sword",
					of(WeaponSubType.SWORD)
			),
			testData(
					"Running",
					of(MovementType.RUNNING)
			),
			testData(
					"Snares",
					of(EffectCategory.SNARES)
			),
			testData(
					"Direct",
					new IsDirect()
			),
			testData(
					"HasHealingComponent",
					new HasHealingComponent()
			),
			testData(
					"IsInstantCast",
					new IsInstantCast()
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
					"HadCrit",
					new HadCrit()
			),
			testData(
					"HasPet",
					new HasPet()
			),
			testData(
					"Owner.HasEffect(Mana Shield)",
					new OwnerHasEffect(MANA_SHIELD)
			),
			testData(
					"Owner.Health% < 35",
					new OwnerHealthPctLessThan(35)
			),
			testData(
					"Target.Health% < 20",
					new TargetHealthPctLessThan(20)
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
					and(AttributeConditions.HEALING, IS_DIRECT)
			),
			testData(
					"Healing & Periodic",
					and(AttributeConditions.HEALING, IS_PERIODIC)
			),
			testData(
					"Shadow | Frost",
					or(AttributeConditions.SHADOW, AttributeConditions.FROST)
			),
			testData(
					"Shadow | Frost | Fire",
					or(of(SHADOW), or(of(FROST), of(FIRE)))
			),
			testData(
					"Physical & Owner.Health% < 40",
					and(AttributeConditions.PHYSICAL, new OwnerHealthPctLessThan(40))
			),
			testData(
					"Physical & Undead, Demon",
					and(AttributeConditions.PHYSICAL, comma(of(UNDEAD), of(DEMON)))
			),
			testData(
					"Direct & Immolate",
					and(new IsDirect(), of(IMMOLATE))
			),
			testData(
					"SpellDamage & Shadow, Fire, Frost",
					and(AttributeConditions.SPELL_DAMAGE, comma(of(SHADOW), of(FIRE), of(FROST)))
			),
			testData(
					"Multi-Shot, Aimed Shot",
					comma(of(AbilityId.of(MULTI_SHOT)), of(AbilityId.of(AIMED_SHOT)))
			)
	);

	record TestData(String string, AttributeCondition condition) {}

	static TestData testData(String string, AttributeCondition condition) {
		return new TestData(string, condition);
	}
}