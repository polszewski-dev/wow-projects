package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.BaseStatInfo;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@Getter
public class AccumulatedSpellStats extends AccumulatedPartialStats {
	private double amount;
	private double amountPct;
	private double effectPct;
	private double power;
	private double powerPct;
	private double powerCoeffPct;
	private double critRating;
	private double critPct;
	private double critEffectPct;
	private double critEffectMultiplierPct;
	private double critCoeffPct;

	public AccumulatedSpellStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedSpellStats(AccumulatedSpellStats stats) {
		super(stats);
		this.amount = stats.amount;
		this.amountPct = stats.amountPct;
		this.effectPct = stats.effectPct;
		this.power = stats.power;
		this.powerPct = stats.powerPct;
		this.powerCoeffPct = stats.powerCoeffPct;
		this.critRating = stats.critRating;
		this.critPct = stats.critPct;
		this.critEffectPct = stats.critEffectPct;
		this.critEffectMultiplierPct = stats.critEffectMultiplierPct;
		this.critCoeffPct = stats.critCoeffPct;
	}

	public void accumulateBaseStatInfo(BaseStatInfo baseStatInfo) {
		critPct += baseStatInfo.getBaseSpellCritPct().value();
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case DAMAGE:
				if (isDamage) {
					this.amount += value;
				}
				break;
			case DAMAGE_PCT, PARTY_DAMAGE_PCT:
				if (isDamage) {
					this.amountPct += value;
				}
				break;
			case HEALING:
				if (isHealing) {
					this.amount += value;
				}
				break;
			case HEALING_PCT:
				if (isHealing) {
					this.amountPct += value;
				}
				break;
			case EFFECT_PCT:
				this.effectPct += value;
				break;
			case POWER, PARTY_POWER:
				this.power += value;
				break;
			case POWER_PCT:
				this.powerPct += value;
				break;
			case POWER_COEFFICIENT_PCT:
				this.powerCoeffPct += value;
				break;
			case CRIT_RATING, PARTY_CRIT_RATING:
				this.critRating += value;
				break;
			case CRIT_PCT, PARTY_CRIT_PCT:
				this.critPct += value;
				break;
			case CRIT_EFFECT_PCT:
				this.critEffectPct += value;
				break;
			case CRIT_EFFECT_MULTIPLIER_PCT:
				this.critEffectMultiplierPct += value;
				break;
			case CRIT_COEFF_PCT:
				this.critCoeffPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedSpellStats copy() {
		return new AccumulatedSpellStats(this);
	}
}
