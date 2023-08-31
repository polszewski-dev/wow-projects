package wow.commons.repository.impl.parser.spell;

import wow.commons.model.buff.*;
import wow.commons.model.buff.impl.BuffImpl;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.repository.impl.SpellRepositoryImpl;

import java.util.List;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.MAX_BUFF_MODIFIER_ATTRIBUTES;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BuffSheetParser extends AbstractSpellSheetParser {
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colExclusionGroup = column("exclusion group");
	private final ExcelColumn colCategories = column("categories");

	public BuffSheetParser(String sheetName, SpellRepositoryImpl spellRepository) {
		super(sheetName, spellRepository);
	}

	@Override
	protected void readSingleRow() {
		Buff buff = getBuff();
		spellRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = colName.getEnum(BuffId::parse);
		var rank = colRank.getInteger(0);
		var type = colType.getEnum(BuffType::parse);
		var exclusionGroup = colExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var pveRoles = getPveRoles();
		var categories = colCategories.getSet(BuffCategory::parse);

		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var characterRestriction = getRestriction();

		var buff = new BuffImpl(
				new BuffIdAndRank(buffId, rank), description, timeRestriction, characterRestriction, type, exclusionGroup, pveRoles, categories
		);

		var effect = getEffect();

		effect.attachSource(new BuffSource(buff));
		buff.setEffect(effect);

		return buff;
	}

	private EffectImpl getEffect() {
		var effect = newEffect();
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var maxStacks = 1;
		var modifierComponent = getModifierComponent(MAX_BUFF_MODIFIER_ATTRIBUTES);

		effect.setDescription(description);
		effect.setTimeRestriction(timeRestriction);
		effect.setMaxStacks(maxStacks);
		effect.setModifierComponent(modifierComponent);
		effect.setStatConversions(List.of());
		effect.setEvents(List.of());
		return effect;
	}
}
