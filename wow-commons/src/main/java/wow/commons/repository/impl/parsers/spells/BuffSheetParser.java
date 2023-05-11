package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffCategory;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.buffs.impl.BuffImpl;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.SpellRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BuffSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colExclusionGroup = column("exclusion group");
	private final ExcelColumn colSourceSpell = column("source spell");
	private final ExcelColumn colCategories = column("categories");

	private final SpellRepositoryImpl spellRepository;

	public BuffSheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName);
		this.spellRepository = spellRepository;
	}

	@Override
	protected void readSingleRow() {
		Buff buff = getBuff();
		spellRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = colId.getInteger();
		var type = colType.getEnum(BuffType::parse);
		var exclusionGroup = colExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var sourceSpell = colSourceSpell.getEnum(SpellId::parse, null);
		var pveRoles = getPveRoles();
		var categories = colCategories.getSet(BuffCategory::parse);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		var buffAttributes = readAttributes();

		var buff = new BuffImpl(
				buffId, description, timeRestriction, characterRestriction, type, exclusionGroup, sourceSpell, pveRoles, categories
		);

		buff.setAttributes(buffAttributes);
		return buff;
	}
}
