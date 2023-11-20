package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.BaseStatInfo;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.AttributeConditionArgs;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@Getter
public class AccumulatedSpellStats extends AccumulatedPartialStats {
	private double damage;
	private double damagePct;
	private double effectPct;
	private double power;
	private double powerPct;
	private double powerCoeffPct;
	private double critRating;
	private double critPct;
	private double critDamagePct;
	private double critDamageMultiplierPct;
	private double critCoeffPct;

	public AccumulatedSpellStats(AttributeConditionArgs conditionArgs, int characterLevel) {
		super(conditionArgs, characterLevel);
	}

	private AccumulatedSpellStats(AccumulatedSpellStats stats) {
		super(stats);
		this.damage = stats.damage;
		this.damagePct = stats.damagePct;
		this.effectPct = stats.effectPct;
		this.power = stats.power;
		this.powerPct = stats.powerPct;
		this.powerCoeffPct = stats.powerCoeffPct;
		this.critRating = stats.critRating;
		this.critPct = stats.critPct;
		this.critDamagePct = stats.critDamagePct;
		this.critDamageMultiplierPct = stats.critDamageMultiplierPct;
		this.critCoeffPct = stats.critCoeffPct;
	}

	public void accumulateBaseStatInfo(BaseStatInfo baseStatInfo) {
		critPct += baseStatInfo.getBaseSpellCritPct().value();
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case DAMAGE:
				this.damage += value;
				break;
			case DAMAGE_PCT:
				this.damagePct += value;
				break;
			case EFFECT_PCT:
				this.effectPct += value;
				break;
			case POWER:
				this.power += value;
				break;
			case POWER_PCT:
				this.powerPct += value;
				break;
			case POWER_COEFFICIENT_PCT:
				this.powerCoeffPct += value;
				break;
			case CRIT_RATING:
				this.critRating += value;
				break;
			case CRIT_PCT:
				this.critPct += value;
				break;
			case CRIT_DAMAGE_PCT:
				this.critDamagePct += value;
				break;
			case CRIT_DAMAGE_MULTIPLIER_PCT:
				this.critDamageMultiplierPct += value;
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
