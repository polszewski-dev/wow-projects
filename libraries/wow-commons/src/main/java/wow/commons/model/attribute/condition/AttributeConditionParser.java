package wow.commons.model.attribute.condition;

import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.DruidFormType;
import wow.commons.model.character.MovementType;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;
import wow.commons.util.condition.ConditionParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
class AttributeConditionParser extends ConditionParser<AttributeCondition> {
	public static AttributeCondition parseCondition(String value) {
		return new AttributeConditionParser(value).parse();
	}

	private AttributeConditionParser(String value) {
		super(value);
	}

	@Override
	protected AttributeCondition orOperator(AttributeCondition left, AttributeCondition right) {
		return ConditionOperator.or(left, right);
	}

	@Override
	protected AttributeCondition andOperator(AttributeCondition left, AttributeCondition right) {
		return ConditionOperator.and(left, right);
	}

	@Override
	protected AttributeCondition commaOperator(List<AttributeCondition> conditions) {
		return ConditionOperator.comma(conditions);
	}

	@Override
	protected AttributeCondition getBasicCondition(String value) {
		if (value.isEmpty()) {
			return AttributeCondition.EMPTY;
		}

		var talentTree = TalentTree.tryParse(value);
		if (talentTree != null) {
			return AttributeCondition.of(talentTree);
		}

		var spellSchool = SpellSchool.tryParse(value);
		if (spellSchool != null) {
			return AttributeCondition.of(spellSchool);
		}

		var abilityId = AbilityId.tryParse(value);

		if (abilityId != null) {
			return AttributeCondition.of(abilityId);
		}

		var abilityCategory = AbilityCategory.tryParse(value);
		if (abilityCategory != null) {
			return AttributeCondition.of(abilityCategory);
		}

		var petType = PetType.tryParse(value);
		if (petType != null) {
			return AttributeCondition.of(petType);
		}

		var creatureType = CreatureType.tryParse(value);
		if (creatureType != null) {
			return AttributeCondition.of(creatureType);
		}

		var druidFormType = DruidFormType.tryParse(value);
		if (druidFormType != null) {
			return AttributeCondition.of(druidFormType);
		}

		var weaponSubType = WeaponSubType.tryParse(value);
		if (weaponSubType != null) {
			return AttributeCondition.of(weaponSubType);
		}

		var professionId = ProfessionId.tryParse(value);
		if (professionId != null) {
			return AttributeCondition.of(professionId);
		}

		var miscCondition = MiscCondition.tryParse(value);
		if (miscCondition != null) {
			return miscCondition;
		}

		var effectCategory = EffectCategory.tryParse(value);
		if (effectCategory != null) {
			return EffectCategoryCondition.of(effectCategory);
		}

		var ownerHasEffect = OwnerHasEffectCondition.tryParse(value);
		if (ownerHasEffect != null) {
			return ownerHasEffect;
		}

		var targetClass = TargetClassCondition.tryParse(value);
		if (targetClass != null) {
			return targetClass;
		}

		var ownerIsChanneling = OwnerIsChannelingCondition.tryParse(value);
		if (ownerIsChanneling != null) {
			return ownerIsChanneling;
		}

		var movementType = MovementType.tryParse(value);
		if (movementType != null) {
			return MovementTypeCondition.of(movementType);
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected AttributeCondition getEmptyCondition() {
		return AttributeCondition.EMPTY;
	}
}
