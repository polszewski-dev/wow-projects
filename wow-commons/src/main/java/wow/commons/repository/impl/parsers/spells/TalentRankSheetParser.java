package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.character.PetType;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.*;
import wow.commons.model.talents.impl.TalentImpl;
import wow.commons.repository.impl.SpellRepositoryImpl;
import wow.commons.util.AttributesBuilder;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentRankSheetParser extends RankedElementSheetParser<TalentId, TalentInfo> {
	private final ExcelColumn colTalent = column("talent");
	private final ExcelColumn colRank = column("rank");

	private final SpellRepositoryImpl spellRepository;

	public TalentRankSheetParser(String sheetName, SpellRepositoryImpl spellRepository, Map<TalentId, List<TalentInfo>> talentInfoById) {
		super(sheetName, talentInfoById);
		this.spellRepository = spellRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		var talentId = colTalent.getEnum(TalentId::parse);

		for (TalentInfo talentInfo : getCorrespondingElements(talentId)) {
			Talent talent = getTalent(talentInfo);
			spellRepository.addTalent(talent);
		}
	}

	private Talent getTalent(TalentInfo talentInfo) {
		var talentId = colTalent.getEnum(TalentId::parse);
		var rank = colRank.getInteger();

		var id = new TalentIdAndRank(talentId, rank);
		var description = getDescription(talentId.getName()).merge(talentInfo.getDescription());
		var timeRestriction = getTimeRestriction().merge(talentInfo.getTimeRestriction());
		var characterRestriction = getRestriction().merge(talentInfo.getCharacterRestriction());
		var talentBenefit = getTalentBenefit();

		var talent = new TalentImpl(id, description, timeRestriction, characterRestriction, talentInfo);

		talent.setAttributes(talentBenefit);
		return talent;
	}

	private Attributes getTalentBenefit() {
		Attributes attributesWithoutConditions = new AttributesBuilder()
				.addAttributes(readAttributes(4))
				.toAttributes();

		return attachConditions(attributesWithoutConditions);
	}

	private Attributes attachConditions(Attributes attributes) {
		AttributesBuilder result = new AttributesBuilder();

		for (AttributeCondition condition : getPossibleConditions()) {
			result.addAttributes(attributes, condition);
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
						.toList();
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (!petTypes.isEmpty()) {
			return petTypes.stream()
					.map(AttributeCondition::of)
					.toList();
		}

		return List.of(AttributeCondition.EMPTY);
	}
}
