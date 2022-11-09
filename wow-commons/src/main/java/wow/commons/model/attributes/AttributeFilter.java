package wow.commons.model.attributes;

import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public class AttributeFilter {
	private final TalentTree talentTree;
	private final SpellSchool spellSchool;
	private final SpellId spellId;
	private final PetType petType;
	private final CreatureType creatureType;

	private AttributeFilter(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		this.talentTree = talentTree;
		this.spellSchool = spellSchool;
		this.spellId = spellId;
		this.petType = petType;
		this.creatureType = creatureType;
	}

	public static AttributeFilter of(SpellSchool spellSchool) {
		return new AttributeFilter(null, spellSchool, null, null, null);
	}

	public static AttributeFilter of(SpellId spellId) {
		return new AttributeFilter(null, null, spellId, null, null);
	}

	public static AttributeFilter ofNotNullOnly(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		return new AttributeFilter(talentTree, spellSchool, spellId, petType, creatureType);
	}

	public boolean matchesCondition(AttributeCondition condition) {
		if (condition == null || condition.isEmpty()) {
			return true;
		}
		if (talentTree != null && !condition.isTheSameOrNull(talentTree)) {
			return false;
		}
		if (spellSchool != null && !condition.isTheSameOrNull(spellSchool)) {
			return false;
		}
		if (spellId != null && !condition.isTheSameOrNull(spellId)) {
			return false;
		}
		if (petType != null && !condition.isTheSameOrNull(petType)) {
			return false;
		}
		return creatureType == null || condition.isTheSameOrNull(creatureType);
	}

	public boolean isEmpty() {
		return talentTree == null && spellSchool == null && spellId == null && petType == null && creatureType == null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AttributeFilter that = (AttributeFilter) o;
		return talentTree == that.talentTree && spellSchool == that.spellSchool && spellId == that.spellId && petType == that.petType && creatureType == that.creatureType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(talentTree, spellSchool, spellId, petType, creatureType);
	}
}
