package wow.commons.repository.impl.parsers;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
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
public class SimpleAttributeParser {
	private final AttributeId attributeId;
	private final List<TalentTree> talentTrees = new ArrayList<>();
	private final List<SpellSchool> spellSchools = new ArrayList<>();
	private final List<SpellId> spellIds = new ArrayList<>();
	private final List<PetType> petTypes = new ArrayList<>();
	private final List<CreatureType> creatureTypes = new ArrayList<>();

	public SimpleAttributeParser(String string) {
		String[] parts = string.split(",");

		this.attributeId = AttributeId.valueOf(parts[0]);

		for (int i = 1; i < parts.length; ++i) {
			String part = parts[i].trim();
			try {
				talentTrees.add(TalentTree.parse(part));
			} catch (IllegalArgumentException e1) {
				try {
					spellSchools.add(SpellSchool.parse(part));
				} catch (IllegalArgumentException e2) {
					try {
						spellIds.add(SpellId.parse(part));
					} catch (IllegalArgumentException e3) {
						try {
							petTypes.add(PetType.parse(part));
						} catch (IllegalArgumentException e4) {
							creatureTypes.add(CreatureType.parse(part));
						}
					}
				}
			}
		}

		ensureAtLeastOneElement(talentTrees);
		ensureAtLeastOneElement(spellSchools);
		ensureAtLeastOneElement(spellIds);
		ensureAtLeastOneElement(petTypes);
		ensureAtLeastOneElement(creatureTypes);
	}

	public List<PrimitiveAttribute> getAttributes(double value) {
		List<PrimitiveAttribute> result = new ArrayList<>();

		for (TalentTree talentTree : talentTrees) {
			for (SpellSchool spellSchool : spellSchools) {
				for (SpellId spellId : spellIds) {
					for (PetType petType : petTypes) {
						for (CreatureType creatureType : creatureTypes) {
							AttributeCondition condition = AttributeCondition.of(talentTree, spellSchool, spellId, petType, creatureType);
							PrimitiveAttribute attribute;

							if (attributeId.isDoubleAttribute()) {
								attribute = Attribute.ofNullable(attributeId, value, condition);
							} else if (attributeId.isPercentAttribute()) {
								attribute = Attribute.ofNullable(attributeId, Percent.ofNullable(value), condition);
							} else {
								throw new IllegalArgumentException("Wrong attribute type: " + attributeId);
							}

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

	private static <T> void ensureAtLeastOneElement(List<T> list) {
		if (list.isEmpty()) {
			list.add(null);
		}
	}
}
