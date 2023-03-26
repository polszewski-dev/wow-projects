package wow.commons.model.attributes;

import wow.commons.model.attributes.condition.*;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2021-10-25
 */
public interface AttributeCondition {
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

	static AttributeCondition of(Profession profession) {
		return profession != null ? ProfessionCondition.of(profession) : EMPTY;
	}

	static AttributeCondition of(ProfessionSpecialization specialization) {
		return specialization != null ? ProfessionSpecCondition.of(specialization) : EMPTY;
	}

	default boolean isEmpty() {
		return false;
	}

	String getConditionString();
}
