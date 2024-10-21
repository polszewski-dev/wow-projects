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

/**
 * User: POlszewski
 * Date: 2021-10-25
 */
public sealed interface AttributeCondition permits
		AbilityCategoryCondition,
		ConditionOperator,
		DruidFormCondition,
		EffectCategoryCondition,
		EmptyCondition,
		MiscCondition,
		MovementTypeCondition,
		OwnerHasEffectCondition,
		OwnerIsChannelingCondition,
		PetTypeCondition,
		ProfessionCondition,
		AbilityIdCondition,
		SpellSchoolCondition,
		TalentTreeCondition,
		TargetClassCondition,
		TargetTypeCondition,
		WeaponTypeCondition {
	AttributeCondition EMPTY = new EmptyCondition();

	static AttributeCondition of(TalentTree talentTree) {
		return talentTree != null ? TalentTreeCondition.of(talentTree) : EMPTY;
	}

	static AttributeCondition of(SpellSchool spellSchool) {
		return spellSchool != null ? SpellSchoolCondition.of(spellSchool) : EMPTY;
	}

	static AttributeCondition of(AbilityId abilityId) {
		return abilityId != null ? AbilityIdCondition.of(abilityId) : EMPTY;
	}

	static AttributeCondition of(AbilityCategory abilityCategory) {
		return abilityCategory != null ? AbilityCategoryCondition.of(abilityCategory) : EMPTY;
	}

	static AttributeCondition of(PetType petType) {
		return petType != null ? PetTypeCondition.of(petType) : EMPTY;
	}

	static AttributeCondition of(CreatureType creatureType) {
		return creatureType != null ? TargetTypeCondition.of(creatureType) : EMPTY;
	}

	static AttributeCondition of(DruidFormType druidFormType) {
		return druidFormType != null ? DruidFormCondition.of(druidFormType) : EMPTY;
	}

	static AttributeCondition of(WeaponSubType weaponSubType) {
		return weaponSubType != null ? WeaponTypeCondition.of(weaponSubType) : EMPTY;
	}

	static AttributeCondition of(ProfessionId professionId) {
		return professionId != null ? ProfessionCondition.of(professionId) : EMPTY;
	}

	static AttributeCondition of(EffectCategory effectCategory) {
		return effectCategory != null ? EffectCategoryCondition.of(effectCategory) : EMPTY;
	}

	static AttributeCondition of(MovementType movementType) {
		return movementType != null ? MovementTypeCondition.of(movementType) : EMPTY;
	}

	static AttributeCondition parse(String value) {
		return new AttributeConditionParser(value).parse();
	}

	default boolean isEmpty() {
		return false;
	}
}
