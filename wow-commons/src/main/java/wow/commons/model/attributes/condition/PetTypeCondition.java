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
public class PetTypeCondition implements AttributeCondition {
	private final PetType petType;

	public PetTypeCondition(PetType petType) {
		this.petType = petType;
		if (petType == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public PetType getPetType() {
		return petType;
	}

	@Override
	public boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return this.petType == petType;
	}

	@Override
	public String toString() {
		return "pet: " + petType;
	}
}
