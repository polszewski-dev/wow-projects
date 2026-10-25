package wow.commons.util.condition;

import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.MovementType;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static wow.commons.model.attribute.AttributeCondition.*;
import static wow.commons.util.condition.AttributeConditionStrings.*;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
public class AttributeConditionParser extends ConditionParser<AttributeCondition, String> {
	public static AttributeCondition parseCondition(String value) {
		return new AttributeConditionParser(value).parse();
	}

	private AttributeConditionParser(String value) {
		super(value);
	}

	@Override
	protected AttributeCondition orOperator(AttributeCondition left, AttributeCondition right) {
		return or(left, right);
	}

	@Override
	protected AttributeCondition andOperator(AttributeCondition left, AttributeCondition right) {
		return and(left, right);
	}

	@Override
	protected AttributeCondition commaOperator(List<AttributeCondition> conditions) {
		return comma(conditions);
	}

	@Override
	protected AttributeCondition getBasicCondition(String value) {
		if (value.isEmpty()) {
			return EMPTY;
		}

		return basicConditionChain
				.tryParse(value)
				.orElseThrow(() -> new IllegalArgumentException("Unknown condition: " + value));
	}

	private final ParseChain<AttributeCondition> basicConditionChain = new ParseChain<>(
			parseAndTransform(ActionType::tryParse, AttributeCondition::of),
			parseAndTransform(PowerType::tryParse, AttributeCondition::of),
			parseAndTransform(TalentTree::tryParse, AttributeCondition::of),
			parseAndTransform(SpellSchool::tryParse, AttributeCondition::of),
			parseAndTransform(AbilityId::tryParse, AttributeCondition::of),
			parseAndTransform(AbilityCategory::tryParse, AttributeCondition::of),
			parseAndTransform(PetType::tryParse, AttributeCondition::of),
			parseAndTransform(CreatureType::tryParse, AttributeCondition::of),
			parseAndTransform(EffectCategory::tryParse, AttributeCondition::of),
			this::tryParseEffectName,
			this::tryParseOwnerHasEffect,
			parseAndTransform(WeaponSubType::tryParse, AttributeCondition::of),
			parseAndTransform(ProfessionId::tryParse, AttributeCondition::of),
			parseAndTransform(MovementType::tryParse, AttributeCondition::of),
			MISC_CONDITIONS::get
	);

	@Override
	protected AttributeCondition getEmptyCondition() {
		return EMPTY;
	}

	@Override
	protected AttributeCondition lessThanOperator(String left, String right) {
		if (OWNER_HEALTH_PCT.equalsIgnoreCase(left)) {
			var pct = Double.parseDouble(right);
			return new OwnerHealthPctLessThan(pct);
		}

		if (TARGET_HEALTH_PCT.equalsIgnoreCase(left)) {
			var pct = Double.parseDouble(right);
			return new TargetHealthPctLessThan(pct);
		}

		throw new IllegalArgumentException("Can't parse: " + left);
	}

	@Override
	protected String getBasicExpression(String value) {
		return value;
	}

	@Override
	protected String getConstant(double value) {
		return value + "";
	}

	private OwnerHasEffect tryParseOwnerHasEffect(String value) {
		return parseStringArgument(value, OWNER_HAS_EFFECT, OwnerHasEffect::new);
	}

	private EffectNameCondition tryParseEffectName(String value) {
		return parseStringArgument(value, EFFECT_NAME, EffectNameCondition::new);
	}

	static final Map<String, AttributeCondition> MISC_CONDITIONS = Map.ofEntries(
			entry(DIRECT, new IsDirect()),
			entry(PERIODIC, new IsPeriodic()),
			entry(HAS_HEALING_COMPONENT, new HasHealingComponent()),
			entry(IS_INSTANT_CAST, new IsInstantCast()),
			entry(HAS_CAST_TIME, new HasCastTime()),
			entry(HAS_CAST_TIME_UNDER_10_SEC, new HasCastTimeUnder10Sec()),
			entry(HAD_CRIT, new HadCrit()),
			entry(HAS_PET, new HasPet())
	);
}
