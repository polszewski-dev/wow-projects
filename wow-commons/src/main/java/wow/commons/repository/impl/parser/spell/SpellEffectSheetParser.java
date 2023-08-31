package wow.commons.repository.impl.parser.spell;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.AbsorptionComponent;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.TickScheme;
import wow.commons.repository.impl.SpellRepositoryImpl;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-31
 */
public class SpellEffectSheetParser extends AbstractSpellSheetParser {
	private final int maxModAttributes;
	private final int maxEvents;

	public SpellEffectSheetParser(String sheetName, SpellRepositoryImpl spellRepository, int maxModAttributes, int maxEvents) {
		super(sheetName, spellRepository);
		this.maxModAttributes = maxModAttributes;
		this.maxEvents = maxEvents;
	}

	@Override
	protected void readSingleRow() {
		Effect effect = getEffect();
		spellRepository.addEffect(effect);
	}

	private final ExcelColumn colMaxStacks = column(STACKS_MAX);
	private final ExcelColumn colTickInterval = column(TICK_INTERVAL);

	private Effect getEffect() {
		var effect = newEffect();
		var effectId = colId.getInteger();
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var maxStacks = colMaxStacks.getInteger();
		var periodicComponent = getPeriodicComponent();
		var tickInterval = colTickInterval.getDuration(null);
		var modifierComponent = getModifierComponent(maxModAttributes);
		var absorptionComponent = getAbsorptionComponent();
		var conversion = getConversion();
		var statConversions = getStatConversions();
		var effectIncreasePerEffectOnTarget = getEffectIncreasePerEffectOnTarget();
		var events = getEvents(maxEvents);

		effect.setEffectId(effectId);
		effect.setDescription(description);
		effect.setTimeRestriction(timeRestriction);
		effect.setMaxStacks(maxStacks);
		effect.setPeriodicComponent(periodicComponent);
		effect.setTickInterval(tickInterval);
		effect.setModifierComponent(modifierComponent);
		effect.setAbsorptionComponent(absorptionComponent);
		effect.setConversion(conversion);
		effect.setStatConversions(statConversions);
		effect.setEffectIncreasePerEffectOnTarget(effectIncreasePerEffectOnTarget);
		effect.setEvents(events);
		return effect;
	}

	private final ExcelColumn colPeriodicType = column(PERIODIC_TYPE).prefixed(PERIODIC_PREFIX);
	private final ExcelColumn colPeriodicAmount = column(PERIODIC_AMOUNT).prefixed(PERIODIC_PREFIX);
	private final ExcelColumn colPeriodicNumTicks = column(PERIODIC_NUM_TICKS).prefixed(PERIODIC_PREFIX);

	private PeriodicComponent getPeriodicComponent() {
		if (colPeriodicType.isEmpty()) {
			return null;
		}

		var type = colPeriodicType.getEnum(ComponentType::parse);
		var coefficient = getCoefficient(PERIODIC_PREFIX);
		var amount = colPeriodicAmount.getInteger();
		var numTicks = colPeriodicNumTicks.getInteger();
		var tickScheme = getTickScheme();

		return new PeriodicComponent(type, coefficient, amount, numTicks, tickScheme);
	}

	private final ExcelColumn colTickWeights = column(PERIODIC_TICK_WEIGHTS).prefixed(PERIODIC_PREFIX);

	private TickScheme getTickScheme() {
		var tickWeights = colTickWeights.getList(Double::parseDouble);

		if (tickWeights.isEmpty()) {
			return TickScheme.DEFAULT;
		}

		return new TickScheme(tickWeights);
	}

	private final ExcelColumn colAbsorbCondition = column(ABSORB_CONDITION).prefixed(ABSORB_PREFIX);
	private final ExcelColumn colAbsorbMin = column(ABSORB_MIN).prefixed(ABSORB_PREFIX);
	private final ExcelColumn colAbsorbMax = column(ABSORB_MAX).prefixed(ABSORB_PREFIX);

	private AbsorptionComponent getAbsorptionComponent() {
		if (colAbsorbMin.isEmpty()) {
			return null;
		}

		var coefficient = getCoefficient(ABSORB_PREFIX);
		var condition = colAbsorbCondition.getEnum(AttributeCondition::parse, AttributeCondition.EMPTY);
		var min = colAbsorbMin.getInteger();
		var max = colAbsorbMax.getInteger();

		return new AbsorptionComponent(coefficient, condition, min, max);
	}
}
