package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.effects.*;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.impl.SpellDataRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EffectSheetParser extends BenefitSheetParser {
	private final ExcelColumn colEffect = column("effect");
	private final ExcelColumn colFriendly = column("friendly");
	private final ExcelColumn colScope = column("scope");
	private final ExcelColumn colMaxStacks = column("max stacks");
	private final ExcelColumn colRemoveAfter = column("remove after");
	private final ExcelColumn colRemoveAfterSchool = column("remove after: school");
	private final ExcelColumn colOnApply = column("on apply");

	private final ExcelColumn colStackScaling = column("stack scaling");

	private final SpellDataRepositoryImpl spellDataRepository;

	public EffectSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colEffect;
	}

	@Override
	protected void readSingleRow() {
		EffectInfo effectInfo = getEffectInfo();
		spellDataRepository.addEffectInfo(effectInfo);
	}

	private EffectInfo getEffectInfo() {
		var effectId = colEffect.getEnum(EffectId::parse);
		var friendly = colFriendly.getBoolean();
		var scope = colScope.getEnum(Scope::parse, Scope.PERSONAL);
		var maxStacks = colMaxStacks.getInteger(1);
		var onApply = colOnApply.getEnum(OnApply::parse, null);
		var stackScaling = colStackScaling.getBoolean();
		var attributes = getEffectAttributes();
		var removeCondition = getRemoveCondition();

		return new EffectInfo(effectId, friendly, scope, maxStacks, removeCondition, onApply, attributes, stackScaling);
	}

	private Attributes getEffectAttributes() {
		return attachConditions(readAttributes(1));
	}

	private RemoveCondition getRemoveCondition() {
		var removeConditionEvent = colRemoveAfter.getEnum(RemoveEvent::parse, null);
		var removeConditionSpellSchool = colRemoveAfterSchool.getEnum(SpellSchool::parse, null);

		return RemoveCondition.create(removeConditionEvent, removeConditionSpellSchool);
	}
}
