package wow.commons.model.attributes.condition;

import lombok.EqualsAndHashCode;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@EqualsAndHashCode
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
	public String toString() {
		return "tree: " + talentTree;
	}
}
