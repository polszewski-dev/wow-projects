package wow.scraper.repository.impl.excel.spell;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.SpellId;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.parser.spell.SpellPatternParams;
import wow.scraper.repository.impl.SpellPatternRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public class SpellPatternSheetParser extends ExcelSheetParser {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colMinDmg = column("min dmg");
	private final ExcelColumn colMaxDmg = column("max dmg");
	private final ExcelColumn colMinDmg2 = column("min dmg2");
	private final ExcelColumn colMaxDmg2 = column("max dmg2");
	private final ExcelColumn colDotDmg = column("dot dmg");
	private final ExcelColumn colTickDmg = column("tick dmg");
	private final ExcelColumn colTickInterval = column("tick interval");
	private final ExcelColumn colDotDuration = column("dot duration");
	private final ExcelColumn colCostAmount = column("cost amount");
	private final ExcelColumn colCostType = column("cost type");
	private final ExcelColumn colTooltip = column("tooltip");

	private final ExcelColumn colReqVersion = column("req_version");

	private final SpellPatternRepositoryImpl spellPatternRepository;

	public SpellPatternSheetParser(String sheetName, SpellPatternRepositoryImpl spellPatternRepository) {
		super(sheetName);
		this.spellPatternRepository = spellPatternRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		var spell = colSpell.getEnum(SpellId::parse);
		var tooltip = colTooltip.getString();
		var params = getParams();
		var reqVersion = colReqVersion.getSet(GameVersionId::parse);
		var spellPattern = new SpellPattern(tooltip.trim(), params, reqVersion);
		spellPatternRepository.add(spell, spellPattern);
	}

	private SpellPatternParams getParams() {
		var minDmg = colMinDmg.getString(null);
		var maxDmg = colMaxDmg.getString(null);
		var minDmg2 = colMinDmg2.getString(null);
		var maxDmg2 = colMaxDmg2.getString(null);
		var dotDmg = colDotDmg.getString(null);
		var tickDmg = colTickDmg.getString(null);
		var tickInterval = colTickInterval.getString(null);
		var dotDuration = colDotDuration.getString(null);
		var costAmount = colCostAmount.getString(null);
		var costType = colCostType.getString(null);

		return new SpellPatternParams(minDmg, maxDmg, minDmg2, maxDmg2, dotDmg, tickDmg, tickInterval, dotDuration, costAmount, costType);
	}
}
