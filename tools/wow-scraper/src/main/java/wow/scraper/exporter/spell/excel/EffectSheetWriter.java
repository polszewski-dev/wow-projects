package wow.scraper.exporter.spell.excel;

import wow.commons.model.effect.Effect;
import wow.commons.model.spell.TickScheme;

import java.util.List;

import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertSizeNoLargerThan;

/**
 * User: POlszewski
 * Date: 2023-08-31
 */
public class EffectSheetWriter extends SpellBaseSheetWriter<Effect, SpellBaseExcelBuilder> {
	private final int maxModAttributes;
	private final int maxEvents;

	public EffectSheetWriter(SpellBaseExcelBuilder builder, int maxModAttributes, int maxEvents) {
		super(builder);
		this.maxModAttributes = maxModAttributes;
		this.maxEvents = maxEvents;
	}

	@Override
	public void writeHeader() {
		setHeader(EFFECT_ID);
		setHeader(NAME, 40);
		writeTimeRestrictionHeader();
		setHeader(STACKS_MAX);
		setHeader(SCOPE);
		setHeader(EXCLUSION_GROUP);
		writePeriodicComponentHeader();
		setHeader(AUGMENTED_ABILITY);
		writeModifierComponentHeader(maxModAttributes);
		writeAbsorptionComponentHeader();
		setHeader(PREVENTED_SCHOOLS);
		writeStatConversionHeader();
		writeEventHeader(maxEvents);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(Effect effect) {
		setValue(effect.getId().value());
		setValue(effect.getName());
		writeTimeRestriction(effect.getTimeRestriction());
		setValue(effect.getMaxStacks());
		setValue(effect.getScope());
		setValue(effect.getExclusionGroup());
		writePeriodicComponent(effect);
		setValue(effect.getAugmentedAbilities());
		writeModifierComponent(effect, maxModAttributes);
		writeAbsorptionComponent(effect);
		setValue(effect.getPreventedSchools());
		writeStatConversions(effect);
		writeEvents(effect, maxEvents);
		writeIconAndTooltip(effect);
	}

	private void writePeriodicComponentHeader() {
		setHeader(PERIODIC_TICK_INTERVAL, PERIODIC_PREFIX);

		for (int i = 1; i <= MAX_PERIODIC_COMMANDS; ++i) {
			writePeriodicCommandHeader(i);
		}
	}

	private void writePeriodicCommandHeader(int idx) {
		var prefix = getPeriodicCommandPrefix(idx);
		
		setHeader(TARGET, prefix);
		setHeader(PERIODIC_TYPE, prefix);
		setHeader(COEFF_VALUE, prefix);
		setHeader(COEFF_SCHOOL, prefix);
		setHeader(PERIODIC_AMOUNT, prefix);
		setHeader(PERIODIC_NUM_TICKS, prefix);
		setHeader(PERIODIC_TICK_WEIGHTS, prefix);
	}

	private void writePeriodicComponent(Effect effect) {
		var periodicComponent = effect.getPeriodicComponent();

		if (periodicComponent != null) {
			setValue(periodicComponent.tickInterval());
		} else {
			setValue((String) null);
		}

		writePeriodicCommands(effect.getPeriodicCommands());
	}

	private void writePeriodicCommands(List<PeriodicCommand> commands) {
		assertSizeNoLargerThan("periodic commands", commands, MAX_PERIODIC_COMMANDS);

		for (int i = 0; i < MAX_PERIODIC_COMMANDS; ++i) {
			if (i < commands.size()) {
				writePeriodicCommand(commands.get(i));
			} else {
				writePeriodicCommand(null);
			}
		}
	}

	private void writePeriodicCommand(PeriodicCommand command) {
		if (command == null) {
			fillRemainingEmptyCols(7);
			return;
		}

		setValue(command.target());
		setValue(command.type());
		setValue(command.coefficient().value());
		setValue(command.school());
		setValue(command.amount());
		setValue(command.numTicks());
		setValue(getTickWeights(command), Object::toString);
	}

	private void writeAbsorptionComponentHeader() {
		var prefix = ABSORB_PREFIX;
		setHeader(COEFF_VALUE, prefix);
		setHeader(COEFF_SCHOOL, prefix);
		setHeader(ABSORB_CONDITION, prefix);
		setHeader(ABSORB_MIN, prefix);
		setHeader(ABSORB_MAX, prefix);
	}

	private void writeAbsorptionComponent(Effect effect) {
		var absorptionComponent = effect.getAbsorptionComponent();

		if (absorptionComponent == null) {
			fillRemainingEmptyCols(5);
			return;
		}

		setValue(absorptionComponent.coefficient().value());
		setValue(absorptionComponent.coefficient().school());
		setValue(absorptionComponent.condition());
		setValue(absorptionComponent.min());
		setValue(absorptionComponent.max());
	}

	private List<Double> getTickWeights(PeriodicCommand command) {
		if (command.tickScheme() == TickScheme.DEFAULT) {
			return List.of();
		}
		return command.tickScheme().tickWeights();
	}
}
