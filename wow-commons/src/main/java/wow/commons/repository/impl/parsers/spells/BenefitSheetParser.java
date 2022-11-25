package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.PetType;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.util.AttributesBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public abstract class BenefitSheetParser extends WowExcelSheetParser {
	protected BenefitSheetParser(String sheetName) {
		super(sheetName);
	}

	protected PrimitiveAttribute getDouble(PrimitiveAttributeId id, String name) {
		var value = column(name).getDouble(0);
		return Attribute.ofNullable(id, value);
	}

	protected Attributes attachConditions(Attributes attributes) {
		AttributesBuilder result = new AttributesBuilder();

		var conditions = getPossibleConditions();

		for (AttributeCondition condition : conditions) {
			for (PrimitiveAttribute attribute : attributes.getPrimitiveAttributeList()) {
				result.addAttribute(attribute.attachCondition(condition));
			}
			for (List<ComplexAttribute> complexAttributes : attributes.getComplexAttributeList().values()) {
				for (ComplexAttribute attribute : complexAttributes) {
					result.addAttribute(attribute.attachCondition(condition));
				}
			}
		}

		return result.toAttributes();
	}

	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colSchool = column("school");
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colPet = column("pet");

	private List<AttributeCondition> getPossibleConditions() {
		var talentTree = colTree.getEnum(TalentTree::parse, null);
		var spellSchool = colSchool.getEnum(SpellSchool::parse, null);
		var spells = colSpell.getSet(SpellId::parse);
		var petTypes = colPet.getSet(PetType::parse);

		if (talentTree != null) {
			if (spellSchool == null && spells.isEmpty() && petTypes.isEmpty()) {
				return List.of(AttributeCondition.of(talentTree));
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (spellSchool != null) {
			if (spells.isEmpty() && petTypes.isEmpty()) {
				return List.of(AttributeCondition.of(spellSchool));
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (!spells.isEmpty()) {
			if (petTypes.isEmpty()) {
				return spells.stream()
						.map(AttributeCondition::of)
						.collect(Collectors.toList());
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (!petTypes.isEmpty()) {
			return petTypes.stream()
					.map(AttributeCondition::of)
					.collect(Collectors.toList());
		}

		return List.of(AttributeCondition.EMPTY);
	}
}
