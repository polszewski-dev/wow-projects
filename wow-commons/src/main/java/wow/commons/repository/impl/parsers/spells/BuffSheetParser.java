package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BuffSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colExclusionGroup = column("exclusion group");
	private final ExcelColumn colDuration = column("duration");
	private final ExcelColumn colCooldown = column("cooldown");
	private final ExcelColumn colSourceSpell = column("source spell");

	private final SpellDataRepositoryImpl spellDataRepository;

	public BuffSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected void readSingleRow() {
		Buff buff = getBuff();
		spellDataRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = colId.getInteger();
		var type = colType.getEnum(BuffType::parse);
		var exclusionGroup = colExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var duration = colDuration.getDuration(null);
		var cooldown = colCooldown.getDuration(null);
		var sourceSpell = colSourceSpell.getEnum(SpellId::parse, null);

		Description description = getDescription();
		Restriction restriction = getRestriction();

		var buffAttributes = getBuffAttributes(duration, cooldown, description.getTooltip());

		return new Buff(buffId, description, restriction, type, exclusionGroup, buffAttributes, sourceSpell, duration, cooldown);
	}

	private Attributes getBuffAttributes(Duration duration, Duration cooldown, String tooltip) {
		Attributes attributes = readAttributes(5);
		if (duration != null && cooldown != null) {
			SpecialAbility onUseAbility = SpecialAbility.onUse(attributes, duration, cooldown, tooltip);
			return Attributes.of(onUseAbility);
		}
		return attributes;
	}
}
