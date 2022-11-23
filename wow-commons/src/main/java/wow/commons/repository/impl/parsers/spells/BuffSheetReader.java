package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.repository.impl.parsers.stats.PrimitiveAttributeSupplier;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelSheetReader;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BuffSheetReader extends ExcelSheetReader {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colLevel = column("level");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colExclusionGroup = column("exclusion group");
	private final ExcelColumn colStat = column("stat");
	private final ExcelColumn colAmount = column("amount");
	private final ExcelColumn colDuration = column("duration");
	private final ExcelColumn colCooldown = column("cooldown");
	private final ExcelColumn colDescription = column("description");
	private final ExcelColumn colSourceSpell = column("source spell");

	private final SpellDataRepositoryImpl spellDataRepository;

	public BuffSheetReader(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Buff buff = getBuff();
		spellDataRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = colId.getInteger();
		var name = colName.getString();
		var level = colLevel.getInteger(0);
		var type = colType.getEnum(BuffType::parse);
		var exclusionGroup = colExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var duration = colDuration.getDuration(null);
		var cooldown = colCooldown.getDuration(null);
		var description = colDescription.getString(null);
		var sourceSpell = colSourceSpell.getEnum(SpellId::parse, null);
		var buffAttributes = getBuffAttributes();

		return new Buff(buffId, name, level, type, exclusionGroup, buffAttributes, sourceSpell, duration, cooldown, description);
	}

	private Attributes getBuffAttributes() {
		AttributesBuilder builder = new AttributesBuilder();
		int maxAttributes = 5;

		for (int statNo = 1; statNo <= maxAttributes; ++statNo) {
			var attributeStr = colStat.multi(statNo).getString(null);
			if (attributeStr != null) {
				PrimitiveAttributeSupplier attributeParser = PrimitiveAttributeSupplier.fromString(attributeStr);
				int amount = colAmount.multi(statNo).getInteger();
				builder.addAttributeList(attributeParser.getAttributeList(amount));
			}
		}

		return builder.toAttributes();
	}
}
