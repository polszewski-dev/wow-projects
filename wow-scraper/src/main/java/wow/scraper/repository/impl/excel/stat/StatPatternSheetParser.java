package wow.scraper.repository.impl.excel.stat;

import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.profession.ProfessionId;
import wow.scraper.parser.stat.StatPattern;
import wow.scraper.parser.stat.StatPatternParams;
import wow.scraper.repository.impl.excel.AbstractPatternSheetParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class StatPatternSheetParser extends AbstractPatternSheetParser {
	private static final int MAX_ATTRIBUTE_PATTERNS = 3;

	private final List<StatPattern> patterns;

	public StatPatternSheetParser(String sheetName, List<StatPattern> patterns) {
		super(sheetName);
		this.patterns = patterns;
	}

	@Override
	protected void readSingleRow() {
		var pattern = getPattern();
		var attributePatterns = getAttributes("", MAX_ATTRIBUTE_PATTERNS);
		var params = getParams();
		var reqVersion = getReqVersion();
		var statPattern = new StatPattern(pattern.trim(), attributePatterns, params, reqVersion);

		patterns.add(statPattern);
	}

	private final ExcelColumn colItemTypes = column("item_types", true);
	private final ExcelColumn colItemSubtypes = column("item_subtypes", true);
	private final ExcelColumn colRequiredProfession = column("req_prof", true);
	private final ExcelColumn colRequiredProfessionLevel = column("req_prof_lvl", true);
	private final ExcelColumn colPveRoles = column("pve_roles", true);

	private StatPatternParams getParams() {
		var itemTypes = colItemTypes.getList(ItemType::parse);
		var itemSubTypes = colItemSubtypes.getList(ItemSubType::parse);
		var reqProfession = colRequiredProfession.getEnum(ProfessionId::parse, null);
		var reqProfessionLvl = colRequiredProfessionLevel.getNullableInteger();
		var pveRoles = colPveRoles.getList(PveRole::parse);

		return new StatPatternParams(itemTypes, itemSubTypes, reqProfession, reqProfessionLvl, pveRoles);
	}
}
