package wow.scraper.exporter.spell.excel;

import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.spell.TickScheme;

import java.util.List;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

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
		writePeriodicComponentHeader();
		setHeader(TICK_INTERVAL);
		setHeader(AUGMENTED_ABILITY);
		writeModifierComponentHeader(maxModAttributes);
		writeAbsorptionComponentHeader();
		writeStatConversionHeader();
		writeEventHeader(maxEvents);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(Effect effect) {
		setValue(effect.getEffectId());
		setValue(effect.getName());
		writeTimeRestriction(effect.getTimeRestriction());
		setValue(effect.getMaxStacks());
		writePeriodicComponent(effect);
		setValue(effect.getTickInterval());
		setValue(effect.getAugmentedAbilities());
		writeModifierComponent(effect, maxModAttributes);
		writeAbsorptionComponent(effect);
		writeStatConversions(effect);
		writeEvents(effect, maxEvents);
		writeIconAndTooltip(effect);
	}

	private void writePeriodicComponentHeader() {
		String prefix = PERIODIC_PREFIX;
		setHeader(PERIODIC_TYPE, prefix);
		setHeader(COEFF_VALUE, prefix);
		setHeader(COEFF_SCHOOL, prefix);
		setHeader(PERIODIC_AMOUNT, prefix);
		setHeader(PERIODIC_NUM_TICKS, prefix);
		setHeader(PERIODIC_TICK_WEIGHTS, prefix);
	}

	private void writePeriodicComponent(Effect effect) {
		var periodicComponent = effect.getPeriodicComponent();

		if (periodicComponent == null) {
			fillRemainingEmptyCols(6);
			return;
		}

		setValue(periodicComponent.type());
		setValue(periodicComponent.coefficient().value());
		setValue(periodicComponent.school());
		setValue(periodicComponent.amount());
		setValue(periodicComponent.numTicks());
		setValue(getTickWeights(periodicComponent), Object::toString);
	}

	private void writeAbsorptionComponentHeader() {
		String prefix = ABSORB_PREFIX;
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

	private List<Double> getTickWeights(PeriodicComponent periodicComponent) {
		if (periodicComponent.tickScheme() == TickScheme.DEFAULT) {
			return List.of();
		}
		return periodicComponent.tickScheme().tickWeights();
	}
}
