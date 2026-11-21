package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.component.StatConversionType;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-10-20
 */
@Getter
public class AccumulatedCostStats extends AccumulatedBaseStats {
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

	private AccumulatedCostStats(AttributeConditionChecker checker) {
		super(checker);
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

	public static AccumulatedCostStats forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, null, spell.getSchool());

		return new AccumulatedCostStats(checker);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case MANA_COST:
				this.manaCost += getValue(attribute, scaleFactor);
				break;
			case MANA_COST_PCT:
				this.manaCostPct += getValue(attribute, scaleFactor);
				break;
			case ENERGY_COST:
				this.energyCost += getValue(attribute, scaleFactor);
				break;
			case ENERGY_COST_PCT:
				this.energyCostPct += getValue(attribute, scaleFactor);
				break;
			case RAGE_COST:
				this.rageCost += getValue(attribute, scaleFactor);
				break;
			case RAGE_COST_PCT:
				this.rageCostPct += getValue(attribute, scaleFactor);
				break;
			case HEALTH_COST:
				this.healthCost += getValue(attribute, scaleFactor);
				break;
			case HEALTH_COST_PCT:
				this.healthCostPct += getValue(attribute, scaleFactor);
				break;
			case COST_REDUCTION_CT:
				this.costReductionPct += getValue(attribute, scaleFactor);
				break;
			case POWER:
				this.power += getValue(attribute, scaleFactor);
				break;
			case COOLDOWN:
				this.cooldown += getValue(attribute, scaleFactor);
				break;
			case COOLDOWN_PCT:
				this.cooldownPct += getValue(attribute, scaleFactor);
				break;
			default:
				super.accumulateAttribute(attribute, scaleFactor);
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

	@Override
	public AccumulatedCostStats copy() {
		return new AccumulatedCostStats(this);
	}

	@Override
	protected double getValueFrom(StatConversionType type) {
		return switch (type) {
			case
					MASTER_POWER_TO_SPELL_DAMAGE,
					MASTER_POWER_TO_ATTACK_POWER

					-> power;

			default

					-> super.getValueFrom(type);
		};
	}
}
