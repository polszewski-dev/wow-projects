package wow.commons.util;

import lombok.AllArgsConstructor;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.spell.SpellId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-10-20
 */
@AllArgsConstructor
public class PrimitiveAttributeSupplier {
	private final PrimitiveAttributeId attributeId;
	private final List<AttributeCondition> conditions;
	private final boolean negate;

	public static PrimitiveAttributeSupplier fromString(String line) {
		boolean negate = false;
		if (line.startsWith("-")) {
			line = line.substring(1);
			negate = true;
		}
		var parser = new Parser(line);
		return new PrimitiveAttributeSupplier(parser.attributeId, parser.conditions, negate);
	}

	public List<PrimitiveAttribute> getAttributeList(double value) {
		if (value == 0) {
			return List.of();
		}

		if (negate) {
			value = -value;
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

	private static class Parser {
		private final List<String> attributeIdParts = new ArrayList<>();
		private final List<AttributeCondition> conditions = new ArrayList<>();
		private final PrimitiveAttributeId attributeId;

		Parser(String line) {
			parse(line);
			this.attributeId = PrimitiveAttributeId.parse(String.join(",", attributeIdParts));

			if (conditions.isEmpty()) {
				conditions.add(AttributeCondition.EMPTY);
			}
		}

		private void parse(String line) {
			for (String part : line.split(",")) {
				parsePart(part.trim());
			}
		}

		private void parsePart(String part) {
			AttributeCondition condition = parseCondition(part);
			if (condition != null) {
				conditions.add(condition);
			} else {
				attributeIdParts.add(part);
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

			return null;
		}
	}
}
