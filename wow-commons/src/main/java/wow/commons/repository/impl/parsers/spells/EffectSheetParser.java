package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.effects.*;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.impl.SpellDataRepositoryImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

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
		return attachConditions(Attributes.of(getPrimitiveAttributes()));
	}

	private List<PrimitiveAttribute> getPrimitiveAttributes() {
		return Stream.of(
				getDouble(EFFECT_INCREASE_PCT, "effect increase%"),
				getDouble(DAMAGE_TAKEN_INCREASE_PCT, "damage taken increase%"),
				getDouble(SPELL_DAMAGE, "sp bonus"),
				getDouble(NUM_NEXT_SPELLS_CAST_INSTANTLY, "#next spells cast instantly")
		)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private RemoveCondition getRemoveCondition() {
		var removeConditionEvent = colRemoveAfter.getEnum(RemoveEvent::parse, null);
		var removeConditionSpellSchool = colRemoveAfterSchool.getEnum(SpellSchool::parse, null);

		return RemoveCondition.create(removeConditionEvent, removeConditionSpellSchool);
	}
}
