package wow.commons.repository.impl.parser.spell;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectExclusionGroup;
import wow.commons.model.effect.EffectScope;
import wow.commons.model.effect.component.AbsorptionComponent;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.TickScheme;
import wow.commons.repository.impl.spell.SpellRepositoryImpl;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-31
 */
public class SpellEffectSheetParser extends AbstractSpellSheetParser {
	private final SpellRepositoryImpl spellRepository;

	private final int maxModAttributes;
	private final int maxEvents;

	public SpellEffectSheetParser(String sheetName, SpellRepositoryImpl spellRepository, int maxModAttributes, int maxEvents) {
		super(sheetName);
		this.spellRepository = spellRepository;
		this.maxModAttributes = maxModAttributes;
		this.maxEvents = maxEvents;
	}

	@Override
	protected void readSingleRow() {
		Effect effect = getEffect();
		spellRepository.addEffect(effect);
	}

	private final ExcelColumn colMaxStacks = column(STACKS_MAX);
	private final ExcelColumn colScope = column(SCOPE);
	private final ExcelColumn colExclusionGroup = column(EXCLUSION_GROUP);

	protected Effect getEffect() {
		var effect = newEffect();
		var effectId = colId.getInteger();
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var maxStacks = colMaxStacks.getInteger();
		var scope = colScope.getEnum(EffectScope::parse);
		var exclusionGroup = colExclusionGroup.getEnum(EffectExclusionGroup::parse, null);
		var periodicComponent = getPeriodicComponent();
		var modifierComponent = getModifierComponent(maxModAttributes);
		var absorptionComponent = getAbsorptionComponent();
		var statConversions = getStatConversions();
		var events = getEvents(maxEvents);

		effect.setEffectId(effectId);
		effect.setDescription(description);
		effect.setTimeRestriction(timeRestriction);
		effect.setMaxStacks(maxStacks);
		effect.setScope(scope);
		effect.setExclusionGroup(exclusionGroup);
		effect.setPeriodicComponent(periodicComponent);
		effect.setModifierComponent(modifierComponent);
		effect.setAbsorptionComponent(absorptionComponent);
		effect.setStatConversions(statConversions);
		effect.setEvents(events);
		return effect;
	}

	private final ExcelColumn colPeriodicType = column(PERIODIC_TYPE, true).prefixed(PERIODIC_PREFIX);
	private final ExcelColumn colPeriodicAmount = column(PERIODIC_AMOUNT).prefixed(PERIODIC_PREFIX);
	private final ExcelColumn colPeriodicNumTicks = column(PERIODIC_NUM_TICKS).prefixed(PERIODIC_PREFIX);
	private final ExcelColumn colTickInterval = column(PERIODIC_TICK_INTERVAL).prefixed(PERIODIC_PREFIX);

	private PeriodicComponent getPeriodicComponent() {
		if (colPeriodicType.isEmpty()) {
			return null;
		}

		var target = getTarget(PERIODIC_PREFIX);
		var type = colPeriodicType.getEnum(ComponentType::parse);
		var coefficient = getCoefficient(PERIODIC_PREFIX);
		var amount = colPeriodicAmount.getInteger();
		var numTicks = colPeriodicNumTicks.getInteger();
		var tickInterval = colTickInterval.getDuration(null);
		var tickScheme = getTickScheme();

		return new PeriodicComponent(target, type, coefficient, amount, numTicks, tickInterval, tickScheme);
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
