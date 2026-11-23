package wow.estimator.model;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.AccumulatedCasterStats;
import wow.character.model.snapshot.AccumulatedStats;
import wow.character.model.snapshot.AccumulatedTargetStats;
import wow.character.service.impl.calc.StatConversionSolver;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.Ability;

import java.util.List;

import static wow.commons.model.spell.component.ComponentCommand.DealDamageDirectly;
import static wow.commons.model.spell.component.ComponentCommand.DealDamagePeriodically;

/**
 * User: POlszewski
 * Date: 2023-11-12
 */
@Getter
@Setter
public class AccumulatedDamagingAbilityStats extends AccumulatedStats {
	private Ability ability;
	private DealDamageDirectly directCommand;
	private DealDamagePeriodically periodicCommand;

	private AccumulatedCasterStats cast;
	private AccumulatedCasterStats cost;
	private AccumulatedTargetStats target;
	private AccumulatedCasterStats hit;
	private AccumulatedCasterStats direct;
	private AccumulatedCasterStats periodic;
	private AccumulatedCasterStats effectDuration;
	private AccumulatedTargetStats receivedEffectStats;

	public AccumulatedDamagingAbilityStats() {}

	private AccumulatedDamagingAbilityStats(AccumulatedDamagingAbilityStats stats) {
		this.ability = stats.ability;
		this.directCommand = stats.directCommand;
		this.periodicCommand = stats.periodicCommand;
		this.cast = stats.cast.copy();
		this.cost = stats.cost.copy();
		this.target = stats.target.copy();
		this.hit = stats.hit.copy();
		this.direct = stats.direct != null ? stats.direct.copy() : null;
		this.periodic = stats.periodic != null ? stats.periodic.copy() : null;
		this.effectDuration = stats.effectDuration != null ? stats.effectDuration.copy() : null;
		this.receivedEffectStats = stats.receivedEffectStats != null ? stats.receivedEffectStats.copy() : null;
	}

	public AccumulatedDamagingAbilityStats copy() {
		return new AccumulatedDamagingAbilityStats(this);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		cast.accumulateAttribute(attribute, scaleFactor);
		cost.accumulateAttribute(attribute, scaleFactor);
		hit.accumulateAttribute(attribute, scaleFactor);

		if (direct != null) {
			direct.accumulateAttribute(attribute, scaleFactor);
		}

		if (periodic != null) {
			periodic.accumulateAttribute(attribute, scaleFactor);
			effectDuration.accumulateAttribute(attribute, scaleFactor);
			receivedEffectStats.accumulateAttribute(attribute, scaleFactor);
		}
	}

	public void increasePower(double value) {
		accumulateAttribute(AttributeId.POWER, value);
	}

	public void solveStatConversions(List<StatConversion> statConversions) {
		if (statConversions.isEmpty()) {
			return;
		}

		solveStatConversions(cast, statConversions);
		solveStatConversions(cost, statConversions);
		solveStatConversions(hit, statConversions);

		if (direct != null) {
			solveStatConversions(direct, statConversions);
		}

		if (periodic != null) {
			solveStatConversions(periodic, statConversions);
			solveStatConversions(effectDuration, statConversions);
		}
	}

	private void solveStatConversions(AccumulatedCasterStats stats, List<StatConversion> statConversions) {
		var solver = new StatConversionSolver(stats.getCaster(), stats, statConversions);

		solver.solve();
	}
}
