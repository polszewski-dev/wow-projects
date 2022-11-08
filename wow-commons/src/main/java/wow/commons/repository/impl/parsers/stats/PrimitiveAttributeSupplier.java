package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
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
	private final AttributeId attributeId;
	private final List<TalentTree> talentTrees = new ArrayList<>();
	private final List<SpellSchool> spellSchools = new ArrayList<>();
	private final List<SpellId> spellIds = new ArrayList<>();
	private final List<PetType> petTypes = new ArrayList<>();
	private final List<CreatureType> creatureTypes = new ArrayList<>();

	private PrimitiveAttributeSupplier(String line) {
		String[] parts = line.split(",");

		this.attributeId = AttributeId.parse(parts[0]);

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
							PrimitiveAttribute attribute = getAttribute(value, talentTree, spellSchool, spellId, petType, creatureType);
							if (attribute != null) {
								result.add(attribute);
							}
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

	private PrimitiveAttribute getAttribute(double value, TalentTree talentTree, SpellSchool spellSchool, SpellId spellId, PetType petType, CreatureType creatureType) {
		AttributeCondition condition = AttributeCondition.of(talentTree, spellSchool, spellId, petType, creatureType);

		if (attributeId.isDoubleAttribute()) {
			return Attribute.ofNullable(attributeId, value, condition);
		} else if (attributeId.isPercentAttribute()) {
			return Attribute.ofNullable(attributeId, Percent.ofNullable(value), condition);
		} else {
			throw new IllegalArgumentException("Wrong attribute type: " + attributeId);
		}
	}

	private void addCondition(String value) {
		try {
			talentTrees.add(TalentTree.parse(value));
		} catch (IllegalArgumentException e1) {
			try {
				spellSchools.add(SpellSchool.parse(value));
			} catch (IllegalArgumentException e2) {
				try {
					spellIds.add(SpellId.parse(value));
				} catch (IllegalArgumentException e3) {
					try {
						petTypes.add(PetType.parse(value));
					} catch (IllegalArgumentException e4) {
						creatureTypes.add(CreatureType.parse(value));
					}
				}
			}
		}
	}

	private static <T> void ensureAtLeastOneElement(List<T> list) {
		if (list.isEmpty()) {
			list.add(null);
		}
	}
}
