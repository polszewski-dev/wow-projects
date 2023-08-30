package wow.scraper.repository.impl.excel;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.scraper.parsers.setters.MiscStatSetter;
import wow.scraper.parsers.setters.StatSetter;
import wow.scraper.parsers.stats.StatPattern;
import wow.scraper.parsers.stats.StatPatternParams;
import wow.scraper.parsers.stats.StatSetterParser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class StatPatternSheetParser extends ExcelSheetParser {
	private final ExcelColumn colPattern = column("pattern");
	private final ExcelColumn colStat1 = column("stat1");
	private final ExcelColumn colStat2 = column("stat2");
	private final ExcelColumn colStat3 = column("stat3", true);

	private final ExcelColumn colSpecialType = column("special:type", true);
	private final ExcelColumn colSpecialStat = column("special:stat", true);
	private final ExcelColumn colSpecialAmount = column("special:amount", true);
	private final ExcelColumn colSpecialDuration = column("special:duration", true);
	private final ExcelColumn colExpression = column("special:expression", true);
	private final ExcelColumn colSpecialItemTypes = column("special:item_types", true);
	private final ExcelColumn colSpecialItemSubtypes = column("special:item_subtypes", true);
	private final ExcelColumn colRequiredProfession = column("special:req_prof", true);
	private final ExcelColumn colRequiredProfessionLevel = column("special:req_prof_lvl", true);
	private final ExcelColumn colPveRoles = column("special:pve_roles", true);

	private final ExcelColumn colReqVersion = column("req_version");

	private final List<StatPattern> patterns;

	public StatPatternSheetParser(String sheetName, List<StatPattern> patterns) {
		super(sheetName);
		this.patterns = patterns;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colPattern;
	}

	@Override
	protected void readSingleRow() {
		var pattern = colPattern.getString();
		var setters = getParsedStatSetters();
		var params = getParams();
		var reqVersion = colReqVersion.getSet(GameVersionId::parse);
		var statPattern = new StatPattern(pattern.trim(), setters, params, reqVersion);
		patterns.add(statPattern);
	}

	private List<StatSetter> getParsedStatSetters() {
		var setters = Stream.of(
						colStat1.getString(null),
						colStat2.getString(null),
						colStat3.getString(null)
				)
				.filter(Objects::nonNull)
				.map(StatSetterParser::new)
				.map(StatSetterParser::parse)
				.toList();

		if (!setters.isEmpty()) {
			return setters;
		}

		return List.of(new MiscStatSetter());
	}

	private StatPatternParams getParams() {
		StatPatternParams params = new StatPatternParams();

		params.setType(colSpecialType.getString(null));
		params.setStatsSupplier(colSpecialStat.getEnum(PrimitiveAttributeSupplier::fromString, null));
		params.setAmount(colSpecialAmount.getString(null));
		params.setDuration(colSpecialDuration.getString(null));
		params.setExpression(colExpression.getString(null));
		params.setItemTypes(colSpecialItemTypes.getList(ItemType::parse));
		params.setItemSubTypes(colSpecialItemSubtypes.getList(ItemSubType::parse));
		params.setRequiredProfession(colRequiredProfession.getEnum(ProfessionId::parse, null));
		params.setRequiredProfessionLevel(colRequiredProfessionLevel.getNullableInteger());
		params.setPveRoles(colPveRoles.getList(PveRole::parse));

		return params;
	}
}
