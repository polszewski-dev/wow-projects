package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.talents.TalentTree;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-28
 */
public class TalentSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colTalent = column("talent");
	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colMaxRank = column("max rank");
	private final ExcelColumn colCalculatorPosition = column("talent calculator position");

	private final Map<TalentId, TalentInfo> talentInfoById;

	public TalentSheetParser(String sheetName, Map<TalentId, TalentInfo> talentInfoById) {
		super(sheetName);
		this.talentInfoById = talentInfoById;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		TalentInfo talentInfo = getTalentInfo();
		talentInfoById.put(talentInfo.getId(), talentInfo);
	}

	private TalentInfo getTalentInfo() {
		var talentId = colTalent.getEnum(TalentId::parse);
		var tree = colTree.getEnum(TalentTree::parse);
		var maxRank = colMaxRank.getInteger();
		var talentCalculatorPosition = colCalculatorPosition.getInteger();

		Description description = getDescription(talentId.getName());
		TimeRestriction timeRestriction = getTimeRestriction();
		CharacterRestriction characterRestriction = getRestriction();

		return new TalentInfo(
				talentId,
				description,
				timeRestriction,
				characterRestriction,
				tree,
				maxRank,
				talentCalculatorPosition
		);
	}
}
