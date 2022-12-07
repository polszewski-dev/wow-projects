package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.talents.TalentTree;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-28
 */
public class TalentSheetParser extends RankedElementSheetParser<TalentId, TalentInfo> {
	private final ExcelColumn colTalent = column("talent");
	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colMaxRank = column("max rank");
	private final ExcelColumn colCalculatorPosition = column("talent calculator position");

	public TalentSheetParser(String sheetName, Map<TalentId, List<TalentInfo>> talentInfoById) {
		super(sheetName, talentInfoById);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		TalentInfo talentInfo = getTalentInfo();
		addElement(talentInfo.getId(), talentInfo);
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
