package wow.commons.model.attributes;

import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-10-25
 */
public class AttributeCondition {
	private final TalentTree talentTree;
	private final SpellSchool spellSchool;
	private final SpellId spellId;
	private final PetType petType;
	private final CreatureType creatureType;

	public static final AttributeCondition EMPTY = new AttributeCondition(null, null, null, null, null);

	private AttributeCondition(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		this.talentTree = talentTree;
		this.spellSchool = spellSchool;
		this.spellId = spellId;
		this.petType = petType;
		this.creatureType = creatureType;
	}

	public static AttributeCondition of(TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		if (talentTree != null || spellSchool != null || spellId != null || petType != null || creatureType != null) {
			return new AttributeCondition(talentTree, spellSchool, spellId, petType, creatureType);
		} else {
			return EMPTY;
		}
	}

	public static AttributeCondition of(SpellInfo spellInfo, PetType petType, CreatureType creatureType) {
		return of(
				spellInfo != null ? spellInfo.getTalentTree() : null,
				spellInfo != null ? spellInfo.getSpellSchool() : null,
				spellInfo != null ? spellInfo.getSpellId() : null,
				petType,
				creatureType
		);
	}

	public static AttributeCondition of(TalentTree talentTree) {
		return of(talentTree, null, null, null, null);
	}

	public static AttributeCondition of(SpellSchool spellSchool) {
		return of(null, spellSchool, null, null, null);
	}

	public static AttributeCondition of(SpellId spellId) {
		return of(null, null, spellId, null, null);
	}

	public static AttributeCondition of(PetType petType) {
		return of(null, null, null, petType, null);
	}

	public static AttributeCondition of(CreatureType creatureType) {
		return of(null, null, null, null, creatureType);
	}

	public TalentTree getTalentTree() {
		return talentTree;
	}

	public SpellSchool getSpellSchool() {
		return spellSchool;
	}

	public SpellId getSpellId() {
		return spellId;
	}

	public PetType getPetType() {
		return petType;
	}

	public CreatureType getCreatureType() {
		return creatureType;
	}

	public boolean isEmpty() {
		return talentTree == null && spellSchool == null && spellId == null && petType == null && creatureType == null;
	}

	public boolean isTheSameOrNull(TalentTree talentTree) {
		return this.talentTree == talentTree || this.talentTree == null;
	}

	public boolean isTheSameOrNull(SpellSchool spellSchool) {
		return this.spellSchool == spellSchool || this.spellSchool == null;
	}

	public boolean isTheSameOrNull(SpellId spellId) {
		return this.spellId == spellId || this.spellId == null;
	}

	public boolean isTheSameOrNull(PetType petType) {
		return this.petType == petType || this.petType == null;
	}

	public boolean isTheSameOrNull(CreatureType creatureType) {
		return this.creatureType == creatureType || this.creatureType == null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AttributeCondition)) return false;
		AttributeCondition that = (AttributeCondition) o;
		return talentTree == that.talentTree &&
				spellSchool == that.spellSchool &&
				spellId == that.spellId &&
				petType == that.petType &&
				creatureType == that.creatureType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(talentTree, spellSchool, spellId, petType, creatureType);
	}

	@Override
	public String toString() {
		return Stream.of(
				talentTree != null ? "tree: " + talentTree : "",
				spellSchool != null ? "school: " + spellSchool : "",
				spellId != null ? "spell: " + spellId : "",
				petType != null ? "pet: " + petType : "",
				creatureType != null ? "creature: " + creatureType : ""
		).filter(x -> !x.isEmpty()).collect(Collectors.joining(", "));
	}
}
