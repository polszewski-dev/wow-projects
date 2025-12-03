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

	@Override
	protected AttributeCondition lessThanOperator(String left, String right) {
		if (OWNER_HEALTH_PCT.equalsIgnoreCase(left)) {
			var pct = Double.parseDouble(right);
			return new OwnerHealthPctLessThan(pct);
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

	static final String OWNER_HEALTH_PCT = "Owner.Health%";

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
