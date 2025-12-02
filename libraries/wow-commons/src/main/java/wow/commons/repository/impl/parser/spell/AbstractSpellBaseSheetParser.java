package wow.commons.repository.impl.parser.spell;

import wow.commons.model.Percent;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.component.*;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.*;
import wow.commons.model.spell.impl.SpellImpl;
import wow.commons.model.spell.impl.TriggeredSpellImpl;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-10-05
 */
public abstract class AbstractSpellBaseSheetParser extends WowExcelSheetParser {
	protected AbstractSpellBaseSheetParser(String sheetName) {
		super(sheetName);
	}

	protected List<StatConversion> getStatConversions(int maxStatConversions) {
		return IntStream.rangeClosed(1, maxStatConversions)
				.mapToObj(this::getStatConversion)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colStatConversionFrom = column(STAT_CONVERSION_FROM);
	private final ExcelColumn colStatConversionTo = column(STAT_CONVERSION_TO);
	private final ExcelColumn colStatConversionToCondition = column(STAT_CONVERSION_TO_CONDITION);
	private final ExcelColumn colStatConversionRatio = column(STAT_CONVERSION_RATIO);

	private StatConversion getStatConversion(int idx) {
		var prefix = getStatConversionPrefix(idx);

		if (colStatConversionFrom.prefixed(prefix).isEmpty()) {
			return null;
		}

		var from = colStatConversionFrom.prefixed(prefix).getEnum(AttributeId::parse);
		var to = colStatConversionTo.prefixed(prefix).getEnum(AttributeId::parse);
		var toCondition = colStatConversionToCondition.prefixed(prefix).getEnum(StatConversionCondition::parse, StatConversionCondition.EMPTY);
		var ratio = colStatConversionRatio.prefixed(prefix).getPercent();

		return new StatConversion(from, to, toCondition, ratio);
	}

	protected EffectImpl getDummyEffect(EffectId effectId) {
		var dummy = new EffectImpl(List.of());
		dummy.setId(effectId);
		return dummy;
	}

	protected final ExcelColumn colAugmentedAbility = column(AUGMENTED_ABILITY, true);

	protected EffectImpl newEffect() {
		var augmentedAbilities = colAugmentedAbility.getList(AbilityId::parse);

		return new EffectImpl(augmentedAbilities);
	}

	protected ModifierComponent getModifierComponent(int maxAttributes) {
		var attributes = readAttributes(MOD_PREFIX, maxAttributes);

		if (attributes.isEmpty()) {
			return null;
		}

		return new ModifierComponent(attributes);
	}

	protected List<Event> getEvents(int maxEvents) {
		return IntStream.rangeClosed(1, maxEvents)
				.mapToObj(this::getEvent)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colEventOn = column(EVENT_ON);
	private final ExcelColumn colEventCondition = column(EVENT_CONDITION);
	private final ExcelColumn colEventChance = column(EVENT_CHANCE_PCT);
	private final ExcelColumn colEventAction = column(EVENT_ACTION);
	private final ExcelColumn colEventTriggeredSpell = column(EVENT_TRIGGERED_SPELL);
	private final ExcelColumn colEventActionParams = column(EVENT_ACTION_PARAMS, true);

	private Event getEvent(int idx) {
		var prefix = getEventPrefix(idx);

		if (colEventOn.prefixed(prefix).isEmpty()) {
			return null;
		}

		var types = colEventOn.prefixed(prefix).getList(EventType::parse);
		var condition = colEventCondition.prefixed(prefix).getEnum(EventCondition::parse, EventCondition.EMPTY);
		var chance = colEventChance.prefixed(prefix).getPercent(Percent._100);
		var actions = colEventAction.prefixed(prefix).getList(EventAction::parse);
		var triggeredSpellId = colEventTriggeredSpell.prefixed(prefix).getNullableInteger(SpellId::ofNullable);
		var actionParams = colEventActionParams.prefixed(prefix).getEnum(EventActionParameters::parse, EventActionParameters.EMPTY);

		var dummy = getDummySpell(triggeredSpellId);

		return new Event(types, condition, chance, actions, dummy, actionParams);
	}

	protected SpellImpl getDummySpell(SpellId spellId) {
		if (spellId == null) {
			return null;
		}
		var dummy = new TriggeredSpellImpl();
		dummy.setId(spellId);
		return dummy;
	}

	private final ExcelColumn colCoeffValue = column(COEFF_VALUE);
	private final ExcelColumn colCoeffSchool = column(COEFF_SCHOOL);

	protected Coefficient getCoefficient(String prefix) {
		var value = colCoeffValue.prefixed(prefix).getPercent(Percent.ZERO);
		var school = colCoeffSchool.prefixed(prefix).getEnum(SpellSchool::parse, null);
		return new Coefficient(value, school);
	}

	private final ExcelColumn colTarget = column(TARGET, true);

	protected SpellTarget getTarget(String prefix) {
		return colTarget.prefixed(prefix).getEnum(SpellTarget::parse);
	}

	protected boolean hasNoTarget(String prefix) {
		return colTarget.prefixed(prefix).isEmpty();
	}
}
