package wow.commons.model.attributes;

import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

/**
 * User: POlszewski
 * Date: 2022-11-03
 */
public interface ConditionalAttribute {
	AttributeCondition getCondition();

	Attribute attachCondition(AttributeCondition condition);

	default boolean hasCondition() {
		return getCondition() != null && !getCondition().isEmpty();
	}

	default boolean isTheSameOrNull(TalentTree talentTree) {
		return !hasCondition() || getCondition().isTheSameOrNull(talentTree);
	}

	default boolean isTheSameOrNull(SpellSchool spellSchool) {
		return !hasCondition() || getCondition().isTheSameOrNull(spellSchool);
	}

	default boolean isTheSameOrNull(SpellId spellId) {
		return !hasCondition() || getCondition().isTheSameOrNull(spellId);
	}

	default boolean isTheSameOrNull(PetType petType) {
		return !hasCondition() || getCondition().isTheSameOrNull(petType);
	}

	default boolean isTheSameOrNull(CreatureType creatureType) {
		return !hasCondition() || getCondition().isTheSameOrNull(creatureType);
	}
}
