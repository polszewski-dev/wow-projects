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
public class AccumulatedBaseStats extends AccumulatedPartialStats {
	private double baseStrength;
	private double baseAgility;
	private double baseStamina;
	private double baseIntellect;
	private double baseSpirit;
	private double strength;
	private double strengthPct;
	private double agility;
	private double agilityPct;
	private double stamina;
	private double staminaPct;
	private double intellect;
	private double intellectPct;
	private double spirit;
	private double spiritPct;
	private double baseStatsPct;
	private double maxHealth;
	private double maxHealthPct;
	private double maxMana;
	private double maxManaPct;

	public AccumulatedBaseStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedBaseStats(AccumulatedBaseStats stats) {
		super(stats);
		this.baseStrength = stats.baseStrength;
		this.baseAgility = stats.baseAgility;
		this.baseStamina = stats.baseStamina;
		this.baseIntellect = stats.baseIntellect;
		this.baseSpirit = stats.baseSpirit;
		this.strength = stats.strength;
		this.strengthPct = stats.strengthPct;
		this.agility = stats.agility;
		this.agilityPct = stats.agilityPct;
		this.stamina = stats.stamina;
		this.staminaPct = stats.staminaPct;
		this.intellect = stats.intellect;
		this.intellectPct = stats.intellectPct;
		this.spirit = stats.spirit;
		this.spiritPct = stats.spiritPct;
		this.baseStatsPct = stats.baseStatsPct;
		this.maxHealth = stats.maxHealth;
		this.maxHealthPct = stats.maxHealthPct;
		this.maxMana = stats.maxMana;
		this.maxManaPct = stats.maxManaPct;
	}

	public void accumulateBaseStatInfo(BaseStatInfo baseStatInfo) {
		baseStrength += baseStatInfo.getBaseStrength();
		baseAgility += baseStatInfo.getBaseAgility();
		baseStamina += baseStatInfo.getBaseStamina();
		baseIntellect += baseStatInfo.getBaseIntellect();
		baseSpirit += baseStatInfo.getBaseSpirit();
		maxHealth += baseStatInfo.getBaseHealth();
		maxMana += baseStatInfo.getBaseMana();
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case STRENGTH:
				this.strength += value;
				break;
			case STRENGTH_PCT:
				this.strengthPct += value;
				break;
			case AGILITY:
				this.agility += value;
				break;
			case AGILITY_PCT:
				this.agilityPct += value;
				break;
			case STAMINA:
				this.stamina += value;
				break;
			case STAMINA_PCT:
				this.staminaPct += value;
				break;
			case INTELLECT:
				this.intellect += value;
				break;
			case INTELLECT_PCT:
				this.intellectPct += value;
				break;
			case SPIRIT:
				this.spirit += value;
				break;
			case SPIRIT_PCT:
				this.spiritPct += value;
				break;
			case BASE_STATS:
				this.baseStrength += value;
				this.baseAgility += value;
				this.baseStamina += value;
				this.baseIntellect += value;
				this.baseSpirit += value;
				break;
			case BASE_STATS_PCT:
				this.baseStatsPct += value;
				break;
			case STATS:
				this.strength += value;
				this.agility += value;
				this.stamina += value;
				this.intellect += value;
				this.spirit += value;
				break;
			case STATS_PCT:
				this.strengthPct += value;
				this.agilityPct += value;
				this.staminaPct += value;
				this.intellectPct += value;
				this.spiritPct += value;
				break;
			case MAX_HEALTH:
				this.maxHealth += value;
				break;
			case MAX_HEALTH_PCT:
				this.maxHealthPct += value;
				break;
			case MAX_MANA:
				this.maxMana += value;
				break;
			case MAX_MANA_PCT:
				this.maxManaPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedBaseStats copy() {
		return new AccumulatedBaseStats(this);
	}

	public double getTotalStrength() {
		return getPrimaryStat(baseStrength, baseStatsPct, strength, strengthPct);
	}

	public double getTotalAgility() {
		return getPrimaryStat(baseAgility, baseStatsPct, agility, agilityPct);
	}

	public double getTotalStamina() {
		return getPrimaryStat(baseStamina, baseStatsPct, stamina, staminaPct);
	}
	
	public double getTotalIntellect() {
		return getPrimaryStat(baseIntellect, baseStatsPct, intellect, intellectPct);
	}

	public double getTotalSpirit() {
		return getPrimaryStat(baseSpirit, baseStatsPct, spirit, spiritPct);
	}

	private double getPrimaryStat(double base, double basePct, double bonus, double bonusPct) {
		return (base * (1 + basePct / 100) + bonus) * (1 + bonusPct / 100);
	}
}
