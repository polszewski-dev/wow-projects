package wow.commons.util.condition;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.character.CreatureType;
import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static wow.commons.model.effect.component.EventCondition.*;
import static wow.commons.util.condition.EventConditionStrings.*;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class EventConditionParser extends ConditionParser<EventCondition, String> {
	public static EventCondition parseCondition(String value) {
		return new EventConditionParser(value).parse();
	}

	private EventConditionParser(String value) {
		super(value);
	}

	@Override
	protected EventCondition orOperator(EventCondition left, EventCondition right) {
		return or(left, right);
	}

	@Override
	protected EventCondition andOperator(EventCondition left, EventCondition right) {
		return and(left, right);
	}

	@Override
	protected EventCondition commaOperator(List<EventCondition> conditions) {
		return comma(conditions);
	}

	@Override
	protected EventCondition getBasicCondition(String value) {
		if (value.isEmpty()) {
			return EMPTY;
		}

		return basicConditionChain
				.tryParse(value)
				.orElseThrow(() -> new IllegalArgumentException("Unknown condition: " + value));
	}

	private final ParseChain<EventCondition> basicConditionChain = new ParseChain<>(
			parseAndTransform(ActionType::tryParse, EventCondition::of),
			parseAndTransform(PowerType::tryParse, EventCondition::of),
			parseAndTransform(TalentTree::tryParse, EventCondition::of),
			parseAndTransform(SpellSchool::tryParse, EventCondition::of),
			parseAndTransform(AbilityId::tryParse, EventCondition::of),
			parseAndTransform(AbilityCategory::tryParse, EventCondition::of),
			parseAndTransform(CreatureType::tryParse, EventCondition::of),
			this::tryParseOwnerHasEffect,
			this::tryParseOwnerIsChanneling,
			this::tryParseHasXFaction,
			MISC_CONDITIONS::get
	);

	@Override
	protected EventCondition getEmptyCondition() {
		return EMPTY;
	}

	@Override
	protected EventCondition lessThanOperator(String left, String right) {
		if (OWNER_HEALTH_PCT.equalsIgnoreCase(left)) {
			var pct = Double.parseDouble(right);
			return new OwnerHealthPctLessThan(pct);
		}
		if (TARGET_HEALTH_PCT.equalsIgnoreCase(left)) {
			var pct = Double.parseDouble(right);
			return new TargetHealthPctLessThan(pct);
		}
		return super.lessThanOperator(left, right);
	}

	@Override
	protected String getBasicExpression(String value) {
		return value;
	}

	@Override
	protected String getConstant(double value) {
		return value + "";
	}

	private OwnerHasEffectCondition tryParseOwnerHasEffect(String value) {
		return parseStringArgument(value, OWNER_HAS_EFFECT, OwnerHasEffectCondition::new);
	}

	private OwnerIsChannelingCondition tryParseOwnerIsChanneling(String value) {
		return parseAbilityIdArgument(value, OWNER_IS_CHANNELING, OwnerIsChannelingCondition::new);
	}

	private HasXFaction tryParseHasXFaction(String value) {
		return parseStringArgument(value, HAS_X_FACTION, HasXFaction::new);
	}

	static final Map<String, EventCondition> MISC_CONDITIONS = Map.ofEntries(
			entry(DIRECT, new IsDirect()),
			entry(PERIODIC, new IsPeriodic()),
			entry(HOSTILE_SPELL, new IsHostileSpell()),
			entry(NORMAL_MELEE_ATTACK, new IsNormalMeleeAttack()),
			entry(SPECIAL_ATTACK, new IsSpecialAttack()),
			entry(HAS_MANA_COST, new HasManaCost()),
			entry(HAS_CAST_TIME, new HasCastTime()),
			entry(HAS_CAST_TIME_UNDER_10_SEC, new HasCastTimeUnder10Sec()),
			entry(CAN_CRIT, new CanCrit()),
			entry(HAD_NO_CRIT, new HadNoCrit()),
			entry(HAD_CRIT, new HadCrit()),
			entry(TARGETING_OTHERS, new IsTargetingOthers()),
			entry(CASTER_IS_OWNER, new CasterIsOwner())
	);
}
