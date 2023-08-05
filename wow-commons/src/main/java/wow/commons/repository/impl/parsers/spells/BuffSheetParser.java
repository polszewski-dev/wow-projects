package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.buffs.*;
import wow.commons.model.buffs.impl.BuffImpl;
import wow.commons.repository.impl.SpellRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BuffSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colBuffId = column("buff");
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colExclusionGroup = column("exclusion group");
	private final ExcelColumn colCategories = column("categories");

	private final SpellRepositoryImpl spellRepository;

	public BuffSheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName);
		this.spellRepository = spellRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colBuffId;
	}

	@Override
	protected void readSingleRow() {
		Buff buff = getBuff();
		spellRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = colBuffId.getEnum(BuffId::parse);
		var rank = colRank.getInteger(0);
		var type = colType.getEnum(BuffType::parse);
		var exclusionGroup = colExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var pveRoles = getPveRoles();
		var categories = colCategories.getSet(BuffCategory::parse);

		var description = getDescription(buffId.getName());
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		var buffAttributes = readAttributes();

		var buff = new BuffImpl(
				new BuffIdAndRank(buffId, rank), description, timeRestriction, characterRestriction, type, exclusionGroup, pveRoles, categories
		);

		buff.setAttributes(buffAttributes);
		return buff;
	}
}
