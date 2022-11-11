package wow.commons.model.attributes;

import wow.commons.model.attributes.condition.*;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

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

	default TalentTree getTalentTree() {
		return null;
	}

	default SpellSchool getSpellSchool() {
		return null;
	}

	default SpellId getSpellId() {
		return null;
	}

	default PetType getPetType() {
		return null;
	}

	default CreatureType getCreatureType() {
		return null;
	}

	default boolean isEmpty() {
		return false;
	}

	default boolean isTheSameOrNull(TalentTree talentTree) {
		return getTalentTree() == talentTree || getTalentTree() == null;
	}

	default boolean isTheSameOrNull(SpellSchool spellSchool) {
		return getSpellSchool() == spellSchool || getSpellSchool() == null;
	}

	default boolean isTheSameOrNull(SpellId spellId) {
		return getSpellId() == spellId || getSpellId() == null;
	}

	default boolean isTheSameOrNull(PetType petType) {
		return getPetType() == petType || getPetType() == null;
	}

	default boolean isTheSameOrNull(CreatureType creatureType) {
		return getCreatureType()  == creatureType || getCreatureType()  == null;
	}

	boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType);

	default boolean isMetBy(SpellInfo spellInfo, PetType petType, CreatureType creatureType) {
		return isMetBy(spellInfo.getTalentTree(), spellInfo.getSpellSchool(), spellInfo.getSpellId(), petType, creatureType);
	}
}
