package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentIdAndRank;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentRankSheetParser extends BenefitSheetParser {
	private final ExcelColumn colTalent = column("talent");
	private final ExcelColumn colRank = column("rank");

	private final SpellDataRepositoryImpl spellDataRepository;

	private final Map<TalentId, TalentInfo> talentInfoById;

	public TalentRankSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository, Map<TalentId, TalentInfo> talentInfoById) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
		this.talentInfoById = talentInfoById;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		Talent talent = getTalent();
		spellDataRepository.addTalent(talent);
	}

	private Talent getTalent() {
		var talentId = colTalent.getEnum(TalentId::parse);
		var rank = colRank.getInteger();

		TalentIdAndRank id = new TalentIdAndRank(talentId, rank);
		TalentInfo talentInfo = talentInfoById.get(talentId);
		Description description = getDescription(talentId.getName()).merge(talentInfo.getDescription());
		TimeRestriction timeRestriction = getTimeRestriction().merge(talentInfo.getTimeRestriction());
		CharacterRestriction characterRestriction = getRestriction().merge(talentInfo.getCharacterRestriction());
		Attributes talentBenefit = getTalentBenefit();

		return new Talent(id, description, timeRestriction, characterRestriction, talentBenefit, talentInfo);
	}

	private Attributes getTalentBenefit() {
		Attributes attributesWithoutConditions = new AttributesBuilder()
				.addAttributes(readAttributes(4))
				.toAttributes();

		return attachConditions(attributesWithoutConditions);
	}
}
