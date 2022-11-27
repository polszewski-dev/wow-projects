package wow.commons.util;

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
	private final List<AttributeCondition> conditions = new ArrayList<>();

	private PrimitiveAttributeSupplier(String line) {
		String[] parts = line.split(",");

		this.attributeId = PrimitiveAttributeId.parse(parts[0]);

		for (int i = 1; i < parts.length; ++i) {
			conditions.add(parseCondition(parts[i].trim()));
		}

		if (conditions.isEmpty()) {
			conditions.add(AttributeCondition.EMPTY);
		}
	}

	private AttributeCondition parseCondition(String value) {
		TalentTree talentTree = TalentTree.tryParse(value);
		if (talentTree != null) {
			return AttributeCondition.of(talentTree);
		}

		SpellSchool spellSchool = SpellSchool.tryParse(value);
		if (spellSchool != null) {
			return AttributeCondition.of(spellSchool);
		}

		SpellId spellId = SpellId.tryParse(value);
		if (spellId != null) {
			return AttributeCondition.of(spellId);
		}

		PetType petType = PetType.tryParse(value);
		if (petType != null) {
			return AttributeCondition.of(petType);
		}

		CreatureType creatureType = CreatureType.tryParse(value);
		if (creatureType != null) {
			return AttributeCondition.of(creatureType);
		}

		throw new IllegalArgumentException(value);
	}

	public static PrimitiveAttributeSupplier fromString(String line) {
		return new PrimitiveAttributeSupplier(line);
	}

	public List<PrimitiveAttribute> getAttributeList(double value) {
		if (value == 0) {
			return List.of();
		}

		List<PrimitiveAttribute> result = new ArrayList<>();

		for (AttributeCondition condition : conditions) {
			result.add(Attribute.of(attributeId, value, condition));
		}

		return result;
	}

	public void addAttributeList(AttributesBuilder builder, double value) {
		builder.addAttributeList(getAttributeList(value));
	}

	public Attributes getAttributes(double value) {
		return Attributes.of(getAttributeList(value));
	}
}
