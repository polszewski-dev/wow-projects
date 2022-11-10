package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.PetType;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-10-20
 */
public class PrimitiveAttributeSupplier {
	private final PrimitiveAttributeId attributeId;
	private final List<TalentTree> talentTrees = new ArrayList<>();
	private final List<SpellSchool> spellSchools = new ArrayList<>();
	private final List<SpellId> spellIds = new ArrayList<>();
	private final List<PetType> petTypes = new ArrayList<>();
	private final List<CreatureType> creatureTypes = new ArrayList<>();

	private PrimitiveAttributeSupplier(String line) {
		String[] parts = line.split(",");

		this.attributeId = PrimitiveAttributeId.parse(parts[0]);

		for (int i = 1; i < parts.length; ++i) {
			addCondition(parts[i].trim());
		}

		ensureAtLeastOneElement(talentTrees);
		ensureAtLeastOneElement(spellSchools);
		ensureAtLeastOneElement(spellIds);
		ensureAtLeastOneElement(petTypes);
		ensureAtLeastOneElement(creatureTypes);
	}

	public static PrimitiveAttributeSupplier fromString(String line) {
		return new PrimitiveAttributeSupplier(line);
	}

	public List<PrimitiveAttribute> getAttributeList(double value) {
		List<PrimitiveAttribute> result = new ArrayList<>();

		for (TalentTree talentTree : talentTrees) {
			for (SpellSchool spellSchool : spellSchools) {
				for (SpellId spellId : spellIds) {
					for (PetType petType : petTypes) {
						for (CreatureType creatureType : creatureTypes) {
							addAttribute(value, result, talentTree, spellSchool, spellId, petType, creatureType);
						}
					}
				}
			}
		}

		return result;
	}

	public Attributes getAttributes(double value) {
		return Attributes.of(getAttributeList(value));
	}

	private void addAttribute(double value, List<PrimitiveAttribute> result, TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		PrimitiveAttribute attribute = getAttribute(value, talentTree, spellSchool, spellId, petType, creatureType);
		if (attribute != null) {
			result.add(attribute);
		}
	}

	private PrimitiveAttribute getAttribute(double value, TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		AttributeCondition condition = AttributeCondition.of(talentTree, spellSchool, spellId, petType, creatureType);

		return Attribute.ofNullable(attributeId, value, condition);
	}

	private void addCondition(String value) {
		TalentTree talentTree = TalentTree.tryParse(value);
		if (talentTree != null) {
			talentTrees.add(talentTree);
			return;
		}

		SpellSchool spellSchool = SpellSchool.tryParse(value);
		if (spellSchool != null) {
			spellSchools.add(spellSchool);
			return;
		}

		SpellId spellId = SpellId.tryParse(value);
		if (spellId != null) {
			spellIds.add(spellId);
			return;
		}

		PetType petType = PetType.tryParse(value);
		if (petType != null) {
			petTypes.add(petType);
			return;
		}

		CreatureType creatureType = CreatureType.tryParse(value);
		if (creatureType != null) {
			creatureTypes.add(creatureType);
			return;
		}

		throw new IllegalArgumentException(value);
	}

	private static <T> void ensureAtLeastOneElement(List<T> list) {
		if (list.isEmpty()) {
			list.add(null);
		}
	}
}
