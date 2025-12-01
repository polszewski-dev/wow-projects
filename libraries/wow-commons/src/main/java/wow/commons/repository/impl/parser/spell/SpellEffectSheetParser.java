package wow.commons.repository.impl.parser.spell;

import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectExclusionGroup;
import wow.commons.model.effect.EffectId;
import wow.commons.model.effect.EffectScope;
import wow.commons.model.effect.component.AbsorptionComponent;
import wow.commons.model.effect.component.AbsorptionCondition;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.TickScheme;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-31
 */
public class SpellEffectSheetParser extends AbstractSpellSheetParser {
	private final SpellExcelParser parser;

	private final int maxModAttributes;
	private final int maxEvents;

	public SpellEffectSheetParser(String sheetName, SpellExcelParser parser, int maxModAttributes, int maxEvents) {
		super(sheetName);
		this.parser = parser;
		this.maxModAttributes = maxModAttributes;
		this.maxEvents = maxEvents;
	}

	@Override
	protected void readSingleRow() {
		var effect = getEffect();
		parser.addEffect(effect);
	}

	private final ExcelColumn colMaxStacks = column(STACKS_MAX);
	private final ExcelColumn colScope = column(SCOPE);
	private final ExcelColumn colExclusionGroup = column(EXCLUSION_GROUP);
	private final ExcelColumn colPreventedSchools = column(PREVENTED_SCHOOLS, true);

	protected Effect getEffect() {
		var effect = newEffect();
		var effectId = colId.getInteger(EffectId::of);
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var maxStacks = colMaxStacks.getInteger();
		var scope = colScope.getEnum(EffectScope::parse);
		var exclusionGroup = colExclusionGroup.getEnum(EffectExclusionGroup::parse, null);
		var periodicComponent = getPeriodicComponent();
		var modifierComponent = getModifierComponent(maxModAttributes);
		var absorptionComponent = getAbsorptionComponent();
		var preventedSchools = colPreventedSchools.getList(SpellSchool::parse);
		var statConversions = getStatConversions();
		var events = getEvents(maxEvents);

		effect.setId(effectId);
		effect.setDescription(description);
		effect.setTimeRestriction(timeRestriction);
		effect.setMaxStacks(maxStacks);
		effect.setScope(scope);
		effect.setExclusionGroup(exclusionGroup);
		effect.setPeriodicComponent(periodicComponent);
		effect.setModifierComponent(modifierComponent);
		effect.setPreventedSchools(preventedSchools);
		effect.setAbsorptionComponent(absorptionComponent);
		effect.setStatConversions(statConversions);
		effect.setEvents(events);
		return effect;
	}

	private final ExcelColumn colTickInterval = column(PERIODIC_TICK_INTERVAL, true).prefixed(PERIODIC_PREFIX);

	private PeriodicComponent getPeriodicComponent() {
		if (colTickInterval.isEmpty()) {
			return null;
		}

		var tickInterval = colTickInterval.getDuration(null);
		var commands = getPeriodicCommands();

		return new PeriodicComponent(commands, tickInterval);
	}

	private List<PeriodicCommand> getPeriodicCommands() {
		return IntStream.rangeClosed(1, MAX_PERIODIC_COMMANDS)
				.mapToObj(this::getPeriodicCommand)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colPeriodicType = column(PERIODIC_TYPE);
	private final ExcelColumn colPeriodicAmount = column(PERIODIC_AMOUNT);
	private final ExcelColumn colPeriodicNumTicks = column(PERIODIC_NUM_TICKS);

	private PeriodicCommand getPeriodicCommand(int idx) {
		var prefix = getPeriodicCommandPrefix(idx);

		var target = getTarget(prefix);

		if (target == null) {
			return null;
		}

		var type = colPeriodicType.prefixed(prefix).getEnum(ComponentType::parse);
		var coefficient = getCoefficient(prefix);
		var amount = colPeriodicAmount.prefixed(prefix).getInteger();
		var numTicks = colPeriodicNumTicks.prefixed(prefix).getInteger();
		var tickScheme = getTickScheme(idx);

		return new PeriodicCommand(target, type, coefficient, amount, numTicks, tickScheme);
	}

	private final ExcelColumn colTickWeights = column(PERIODIC_TICK_WEIGHTS);

	private TickScheme getTickScheme(int idx) {
		var prefix = getPeriodicCommandPrefix(idx);
		var tickWeights = colTickWeights.prefixed(prefix).getList(Double::parseDouble);

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
		var condition = colAbsorbCondition.getEnum(AbsorptionCondition::parse, AbsorptionCondition.EMPTY);
		var min = colAbsorbMin.getInteger();
		var max = colAbsorbMax.getInteger();

		return new AbsorptionComponent(coefficient, condition, min, max);
	}
}
