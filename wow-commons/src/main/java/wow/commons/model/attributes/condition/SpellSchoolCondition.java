package wow.commons.model.attributes.condition;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public class SpellSchoolCondition implements AttributeCondition {
	private final SpellSchool spellSchool;

	public SpellSchoolCondition(SpellSchool spellSchool) {
		this.spellSchool = spellSchool;
		if (spellSchool == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public SpellSchool getSpellSchool() {
		return spellSchool;
	}

	@Override
	public boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return this.spellSchool == spellSchool;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SpellSchoolCondition)) return false;
		SpellSchoolCondition that = (SpellSchoolCondition) o;
		return spellSchool == that.spellSchool;
	}

	@Override
	public int hashCode() {
		return Objects.hash(spellSchool);
	}

	@Override
	public String toString() {
		return "school: " + spellSchool;
	}
}
