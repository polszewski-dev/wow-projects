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
import static wow.commons.util.parser.ParserUtil.parseMultipleValues;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
public class AttributeConditionParser extends ConditionParser<AttributeCondition, Void> {
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

		var petType = PetType.tryParse(value);

		if (petType != null) {
			return of(petType);
		}

		var creatureType = CreatureType.tryParse(value);

		if (creatureType != null) {
			return of(creatureType);
		}

		var effectCategory = EffectCategory.tryParse(value);

		if (effectCategory != null) {
			return of(effectCategory);
		}

		var ownerHealthBelowPct = tryParseOwnerHealthBelowPct(value);

		if (ownerHealthBelowPct != null) {
			return ownerHealthBelowPct;
		}

		var weaponSubType = WeaponSubType.tryParse(value);

		if (weaponSubType != null) {
			return of(weaponSubType);
		}

		var professionId = ProfessionId.tryParse(value);

		if (professionId != null) {
			return of(professionId);
		}

		var movementType = MovementType.tryParse(value);

		if (movementType != null) {
			return of(movementType);
		}

		var miscCondition = MISC_CONDITIONS.get(value);

		if (miscCondition != null) {
			return miscCondition;
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected AttributeCondition getEmptyCondition() {
		return EMPTY;
	}

	private OwnerHealthBelowPct tryParseOwnerHealthBelowPct(String value) {
		var healthPct = parsePercent(OWNER_HEALTH_BELOW_PREFIX, value);

		if (healthPct == null) {
			return null;
		}

		return new OwnerHealthBelowPct(healthPct);
	}

	private Integer parsePercent(String prefix, String value) {
		var result = parseMultipleValues(prefix + "(\\d+)%", value);

		if (result.isEmpty()) {
			return null;
		}

		return Integer.parseInt(result.get(0));
	}

	static final String OWNER_HEALTH_BELOW_PREFIX = "OwnerHealthBelow";

	static final Map<String, AttributeCondition> MISC_CONDITIONS;

	static {
		MISC_CONDITIONS = Map.ofEntries(
				entry("Direct", IS_DIRECT),
				entry("HasHealingComponent", HAS_HEALING_COMPONENT),
				entry("IsInstantCast", IS_INSTANT_CAST),
				entry("HasCastTime", HAS_CAST_TIME),
				entry("HasCastTimeUnder10Sec", HAS_CAST_TIME_UNDER_10_SEC),
				entry("HadCrit", HAD_CRIT),
				entry("HasPet", HAS_PET)
		);
	}
}
