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
public class TalentTreeCondition implements AttributeCondition {
	private final TalentTree talentTree;

	public TalentTreeCondition(TalentTree talentTree) {
		this.talentTree = talentTree;
		if (talentTree == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public TalentTree getTalentTree() {
		return talentTree;
	}

	@Override
	public boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return this.talentTree == talentTree;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TalentTreeCondition)) return false;
		TalentTreeCondition that = (TalentTreeCondition) o;
		return talentTree == that.talentTree;
	}

	@Override
	public int hashCode() {
		return Objects.hash(talentTree);
	}

	@Override
	public String toString() {
		return "tree: " + talentTree;
	}
}
