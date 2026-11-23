package wow.estimator.model;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.AccumulatedCasterStats;
import wow.character.model.snapshot.AccumulatedStats;
import wow.character.model.snapshot.AccumulatedTargetStats;
import wow.character.util.StatConversionConditionChecker;
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

	private boolean toConditionMatches(StatConversion statConversion) {
		var checker = new StatConversionConditionChecker(direct.getActionType(), direct.getPowerType());

		return checker.check(statConversion);
	}

	public void increasePower(double value) {
		accumulateAttribute(AttributeId.POWER, value);
	}

	public void solveStatConversions(List<StatConversion> statConversions) {
		for (var statConversion : statConversions) {
			if (statConversion.isFromOwner() && toConditionMatches(statConversion)) {
				accumulateConvertedStat(statConversion);
			}
		}
	}

	private void accumulateConvertedStat(StatConversion statConversion) {
		cast.accumulateConvertedAttribute(statConversion);
		cost.accumulateConvertedAttribute(statConversion);
		hit.accumulateConvertedAttribute(statConversion);

		if (direct != null) {
			direct.accumulateConvertedAttribute(statConversion);
		}

		if (periodic != null) {
			periodic.accumulateConvertedAttribute(statConversion);
			effectDuration.accumulateConvertedAttribute(statConversion);
			receivedEffectStats.accumulateConvertedAttribute(statConversion);
		}
	}
}
