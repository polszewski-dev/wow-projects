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
public class CreatureTypeCondition implements AttributeCondition {
	private final CreatureType creatureType;

	public CreatureTypeCondition(CreatureType creatureType) {
		this.creatureType = creatureType;
		if (creatureType == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public CreatureType getCreatureType() {
		return creatureType;
	}

	@Override
	public boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return this.creatureType == creatureType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CreatureTypeCondition)) return false;
		CreatureTypeCondition that = (CreatureTypeCondition) o;
		return creatureType == that.creatureType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(creatureType);
	}

	@Override
	public String toString() {
		return "creature: " + creatureType;
	}
}
