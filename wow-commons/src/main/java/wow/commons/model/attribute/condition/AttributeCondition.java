package wow.commons.model.attribute.condition;

import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2021-10-25
 */
public sealed interface AttributeCondition permits
		CreatureTypeCondition, EmptyCondition, PetTypeCondition, ProfessionCondition, ProfessionSpecCondition,
		SpellIdCondition, SpellSchoolCondition, TalentTreeCondition {

	AttributeCondition EMPTY = new EmptyCondition();

	static AttributeCondition of(TalentTree talentTree) {
		return talentTree != null ? TalentTreeCondition.of(talentTree) : EMPTY;
	}

	static AttributeCondition of(SpellSchool spellSchool) {
		return spellSchool != null ? SpellSchoolCondition.of(spellSchool) : EMPTY;
	}

	static AttributeCondition of(SpellId spellId) {
		return spellId != null ? SpellIdCondition.of(spellId) : EMPTY;
	}

	static AttributeCondition of(PetType petType) {
		return petType != null ? PetTypeCondition.of(petType) : EMPTY;
	}

	static AttributeCondition of(CreatureType creatureType) {
		return creatureType != null ? CreatureTypeCondition.of(creatureType) : EMPTY;
	}

	static AttributeCondition of(ProfessionId professionId) {
		return professionId != null ? ProfessionCondition.of(professionId) : EMPTY;
	}

	static AttributeCondition of(ProfessionSpecializationId specializationId) {
		return specializationId != null ? ProfessionSpecCondition.of(specializationId) : EMPTY;
	}

	default boolean isEmpty() {
		return false;
	}

	String getConditionString();
}
