package wow.commons.util.condition;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.DruidFormType;
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
import static wow.commons.util.parser.ParserUtil.parseMultipleValues;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class EventConditionParser extends ConditionParser<EventCondition, Void> {
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

		var actionType = ActionType.tryParse(value);

		if (actionType != null) {
			return of(actionType);
		}

		var powerType = PowerType.tryParse(value);

		if (powerType != null) {
			return of(powerType);
		}

		var talentTree = TalentTree.tryParse(value);

		if (talentTree != null) {
			return of(talentTree);
		}

		var spellSchool = SpellSchool.tryParse(value);

		if (spellSchool != null) {
			return of(spellSchool);
		}

		var abilityId = AbilityId.tryParse(value);

		if (abilityId != null) {
			return of(abilityId);
		}

		var abilityCategory = AbilityCategory.tryParse(value);

		if (abilityCategory != null) {
			return of(abilityCategory);
		}

		var creatureType = CreatureType.tryParse(value);

		if (creatureType != null) {
			return of(creatureType);
		}

		var druidFormType = DruidFormType.tryParse(value);

		if (druidFormType != null) {
			return of(druidFormType);
		}

		var ownerHasEffect = tryParseOwnerHasEffect(value);

		if (ownerHasEffect != null) {
			return ownerHasEffect;
		}

		var ownerIsChanneling = tryParseOwnerIsChanneling(value);

		if (ownerIsChanneling != null) {
			return ownerIsChanneling;
		}

		var targetClass = tryParseTargetClass(value);

		if (targetClass != null) {
			return targetClass;
		}

		var ownerHealthBelowPct = tryParseOwnerHealthBelowPct(value);

		if (ownerHealthBelowPct != null) {
			return ownerHealthBelowPct;
		}

		var targetHealthBelowPct = tryParseTargetHealthBelowPct(value);

		if (targetHealthBelowPct != null) {
			return targetHealthBelowPct;
		}

		var miscCondition = MISC_CONDITIONS.get(value);

		if (miscCondition != null) {
			return miscCondition;
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected EventCondition getEmptyCondition() {
		return EMPTY;
	}

	private TargetClassCondition tryParseTargetClass(String value) {
		if (value != null && value.startsWith(TARGET_CLASS_PREFIX)) {
			var characterClassIdStr = withoutPrefix(value, TARGET_CLASS_PREFIX);
			var characterClassId = CharacterClassId.parse(characterClassIdStr);

			return new TargetClassCondition(characterClassId);
		}

		return null;
	}

	private OwnerHasEffectCondition tryParseOwnerHasEffect(String value) {
		if (value != null && value.startsWith(OWNER_HAS_EFFECT_PREFIX)) {
			var abilityIdStr = withoutPrefix(value, OWNER_HAS_EFFECT_PREFIX);
			var abilityId = AbilityId.parse(abilityIdStr);

			return new OwnerHasEffectCondition(abilityId);
		}

		return null;
	}

	private OwnerIsChannelingCondition tryParseOwnerIsChanneling(String value) {
		if (value != null && value.startsWith(OWNER_IS_CHANNELING_PREFIX)) {
			var abilityIdStr = withoutPrefix(value, OWNER_IS_CHANNELING_PREFIX);
			var abilityId = AbilityId.parse(abilityIdStr);

			return new OwnerIsChannelingCondition(abilityId);
		}
		return null;
	}

	private OwnerHealthBelowPct tryParseOwnerHealthBelowPct(String value) {
		var healthPct = parsePercent(OWNER_HEALTH_BELOW_PREFIX, value);

		if (healthPct == null) {
			return null;
		}

		return new OwnerHealthBelowPct(healthPct);
	}

	private TargetHealthBelowPct tryParseTargetHealthBelowPct(String value) {
		var healthPct = parsePercent(TARGET_HEALTH_BELOW_PREFIX, value);

		if (healthPct == null) {
			return null;
		}

		return new TargetHealthBelowPct(healthPct);
	}

	private Integer parsePercent(String prefix, String value) {
		var result = parseMultipleValues(prefix + "(\\d+)%", value);

		if (result.isEmpty()) {
			return null;
		}

		return Integer.parseInt(result.get(0));
	}

	private String withoutPrefix(String value, String targetClassPrefix) {
		return value.replace(targetClassPrefix, "").trim();
	}

	static final String TARGET_CLASS_PREFIX = "TargetClass=";
	static final String OWNER_HAS_EFFECT_PREFIX = "OwnerHas:";
	static final String OWNER_IS_CHANNELING_PREFIX = "OwnerIsChanneling:";

	static final String OWNER_HEALTH_BELOW_PREFIX = "OwnerHealthBelow";
	static final String TARGET_HEALTH_BELOW_PREFIX = "TargetHealthBelow";

	static final Map<String, EventCondition> MISC_CONDITIONS;

	static {
		MISC_CONDITIONS = Map.ofEntries(
				entry("Direct", IS_DIRECT),
				entry("Periodic", IS_PERIODIC),
				entry("HostileSpell", IS_HOSTILE_SPELL),
				entry("NormalMeleeAttack", IS_NORMAL_MELEE_ATTACK),
				entry("SpecialAttack", IS_SPECIAL_ATTACK),
				entry("HasManaCost", HAS_MANA_COST),
				entry("HasCastTime", HAS_CAST_TIME),
				entry("HasCastTimeUnder10Sec", HAS_CAST_TIME_UNDER_10_SEC),
				entry("CanCrit", CAN_CRIT),
				entry("HadNoCrit", HAD_NO_CRIT),
				entry("TargetingOthers", IS_TARGETING_OTHERS)
		);
	}
}
