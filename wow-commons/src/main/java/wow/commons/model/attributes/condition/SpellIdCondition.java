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
public class SpellIdCondition implements AttributeCondition {
	private final SpellId spellId;

	public SpellIdCondition(SpellId spellId) {
		this.spellId = spellId;
		if (spellId == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public SpellId getSpellId() {
		return spellId;
	}

	@Override
	public boolean isMetBy(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return this.spellId == spellId;
	}

	@Override
	public String toString() {
		return "spell: " + spellId;
	}
}
