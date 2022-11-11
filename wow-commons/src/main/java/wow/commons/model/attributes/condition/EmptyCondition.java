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
public class EmptyCondition implements AttributeCondition {
	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return o instanceof EmptyCondition;
	}

	@Override
	public int hashCode() {
		return Objects.hash(0);
	}

	@Override
	public String toString() {
		return "";
	}
}
