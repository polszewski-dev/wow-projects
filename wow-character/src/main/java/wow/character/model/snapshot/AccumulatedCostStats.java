package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.spell.ResourceType;

/**
 * User: POlszewski
 * Date: 2023-10-20
 */
@Getter
public class AccumulatedCostStats extends AccumulatedPartialStats {
	private double manaCost;
	private double manaCostPct;
	private double energyCost;
	private double energyCostPct;
	private double rageCost;
	private double rageCostPct;
	private double healthCost;
	private double healthCostPct;
	private double costReductionPct;
	private double power;
	private double cooldown;
	private double cooldownPct;

	public AccumulatedCostStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedCostStats(AccumulatedCostStats stats) {
		super(stats);
		this.manaCost = stats.manaCost;
		this.manaCostPct = stats.manaCostPct;
		this.energyCost = stats.energyCost;
		this.energyCostPct = stats.energyCostPct;
		this.rageCost = stats.rageCost;
		this.rageCostPct = stats.rageCostPct;
		this.healthCost = stats.healthCost;
		this.healthCostPct = stats.healthCostPct;
		this.costReductionPct = stats.costReductionPct;
		this.power = stats.power;
		this.cooldown = stats.cooldown;
		this.cooldownPct = stats.cooldownPct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case MANA_COST:
				this.manaCost += value;
				break;
			case MANA_COST_PCT:
				this.manaCostPct += value;
				break;
			case ENERGY_COST:
				this.energyCost += value;
				break;
			case ENERGY_COST_PCT:
				this.energyCostPct += value;
				break;
			case RAGE_COST:
				this.rageCost += value;
				break;
			case RAGE_COST_PCT:
				this.rageCostPct += value;
				break;
			case HEALTH_COST:
				this.healthCost += value;
				break;
			case HEALTH_COST_PCT:
				this.healthCostPct += value;
				break;
			case COST_REDUCTION_CT:
				this.costReductionPct += value;
				break;
			case POWER:
				this.power += value;
				break;
			case COOLDOWN:
				this.cooldown += value;
				break;
			case COOLDOWN_PCT:
				this.cooldownPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public double getCost(ResourceType resourceType) {
		return switch (resourceType) {
			case MANA -> manaCost;
			case ENERGY -> energyCost;
			case RAGE -> rageCost;
			case HEALTH -> healthCost;
			case PET_MANA -> throw new UnsupportedOperationException();
		};
	}

	public double getCostPct(ResourceType resourceType) {
		return switch (resourceType) {
			case MANA -> manaCostPct;
			case ENERGY -> energyCostPct;
			case RAGE -> rageCostPct;
			case HEALTH -> healthCostPct;
			case PET_MANA -> throw new UnsupportedOperationException();
		};
	}

	public AccumulatedCostStats copy() {
		return new AccumulatedCostStats(this);
	}
}
