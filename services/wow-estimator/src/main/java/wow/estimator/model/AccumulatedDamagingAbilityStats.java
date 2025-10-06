package wow.estimator.model;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.*;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.effect.component.StatConversionCondition;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.component.DirectComponent;

/**
 * User: POlszewski
 * Date: 2023-11-12
 */
@Getter
@Setter
public class AccumulatedDamagingAbilityStats extends AccumulatedStats {
	private Ability ability;
	private DirectComponent directComponent;
	private PeriodicComponent periodicComponent;

	private AccumulatedCastStats cast;
	private AccumulatedCostStats cost;
	private AccumulatedTargetStats target;
	private AccumulatedHitStats hit;
	private AccumulatedSpellStats direct;
	private AccumulatedSpellStats periodic;
	private AccumulatedDurationStats effectDuration;

	public AccumulatedDamagingAbilityStats(AttributeScalingParams scalingParams) {
		super(scalingParams);
	}

	private AccumulatedDamagingAbilityStats(AccumulatedDamagingAbilityStats stats) {
		this(stats.scalingParams);
		this.ability = stats.ability;
		this.directComponent = stats.directComponent;
		this.periodicComponent = stats.periodicComponent;
		this.cast = stats.cast.copy();
		this.cost = stats.cost.copy();
		this.target = stats.target.copy();
		this.hit = stats.hit.copy();
		this.direct = stats.direct != null ? stats.direct.copy() : null;
		this.periodic = stats.periodic != null ? stats.periodic.copy() : null;
		this.effectDuration = stats.effectDuration != null ? stats.effectDuration.copy() : null;
	}

	public AccumulatedDamagingAbilityStats copy() {
		return new AccumulatedDamagingAbilityStats(this);
	}

	@Override
	protected void accumulateAttribute(Attribute attribute, double scaleFactor) {
		var id = attribute.id();
		var value = scaleFactor * attribute.getScaledValue(scalingParams);
		var condition = attribute.condition();

		accumulateAttribute(id, value, condition);
	}

	@Override
	protected void accumulateConvertedStat(StatConversion statConversion, BaseStatsSnapshot baseStats) {
		var valueFrom = getAccumulatedValue(statConversion.from(), baseStats);
		var ratio = statConversion.ratioPct().value() / 100;
		var valueTo = valueFrom * ratio;
		var condition = statConversion.toCondition();

		accumulateAttribute(statConversion.to(), valueTo, condition);
	}

	public void accumulateAttribute(AttributeId id, double value, AttributeCondition condition) {
		cast.accumulateAttribute(id, value, condition);
		cost.accumulateAttribute(id, value, condition);
		hit.accumulateAttribute(id, value, condition);

		if (direct != null) {
			direct.accumulateAttribute(id, value, condition);
		}

		if (periodic != null) {
			periodic.accumulateAttribute(id, value, condition);
			effectDuration.accumulateAttribute(id, value, condition);
		}
	}

	public void accumulateAttribute(AttributeId id, double value, StatConversionCondition condition) {
		cast.accumulateAttribute(id, value, condition);
		cost.accumulateAttribute(id, value, condition);
		hit.accumulateAttribute(id, value, condition);

		if (direct != null) {
			direct.accumulateAttribute(id, value, condition);
		}

		if (periodic != null) {
			periodic.accumulateAttribute(id, value, condition);
			effectDuration.accumulateAttribute(id, value, condition);
		}
	}

	public void increasePower(double value) {
		accumulateAttribute(AttributeId.POWER, value, AttributeCondition.EMPTY);
	}
}
