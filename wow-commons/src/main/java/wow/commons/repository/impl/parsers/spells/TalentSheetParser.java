package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentIdAndRank;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentSheetParser extends BenefitSheetParser {
	private final ExcelColumn colTalent = column("talent");
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colMaxRank = column("max rank");
	private final ExcelColumn colCalculatorPosition = column("talent calculator position");

	private final SpellDataRepositoryImpl spellDataRepository;

	public TalentSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		Talent talent = getTalent();
		Attributes talentBenefit = getTalentBenefit();
		spellDataRepository.addTalent(talent, talentBenefit);
	}

	private Talent getTalent() {
		var talentId = colTalent.getEnum(TalentId::parse);
		var rank = colRank.getInteger();
		var maxRank = colMaxRank.getInteger();
		var talentCalculatorPosition = colCalculatorPosition.getInteger();

		TalentIdAndRank id = new TalentIdAndRank(talentId, rank);
		Description description = getDescription(talentId.getName());
		Restriction restriction = getRestriction();

		return new Talent(id, description, restriction, Attributes.EMPTY, maxRank, talentCalculatorPosition);
	}

	private Attributes getTalentBenefit() {
		Attributes attributesWithoutConditions = new AttributesBuilder()
				.addAttributes(readAttributes(4))
				.toAttributes();

		return attachConditions(attributesWithoutConditions);
	}
}
