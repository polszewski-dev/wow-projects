package wow.commons.model.attributes;

import wow.commons.model.attributes.condition.*;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
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
		return talentTree != null ? new TalentTreeCondition(talentTree) : EMPTY;
	}

	static AttributeCondition of(SpellSchool spellSchool) {
		return spellSchool != null ? new SpellSchoolCondition(spellSchool) : EMPTY;
	}

	static AttributeCondition of(SpellId spellId) {
		return spellId != null ? new SpellIdCondition(spellId) : EMPTY;
	}

	static AttributeCondition of(PetType petType) {
		return petType != null ? new PetTypeCondition(petType) : EMPTY;
	}

	static AttributeCondition of(CreatureType creatureType) {
		return creatureType != null ? new CreatureTypeCondition(creatureType) : EMPTY;
	}

	default boolean isEmpty() {
		return false;
	}

	String getConditionString();
}
