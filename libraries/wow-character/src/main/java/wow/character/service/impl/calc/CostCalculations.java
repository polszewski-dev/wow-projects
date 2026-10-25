package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedCostStats;
import wow.character.model.snapshot.SpellCostSnapshot;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.spell.Ability;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class CostCalculations extends Calculations {
	public static AccumulatedCostStats newAccumulatedCostStats(Character caster, Ability ability, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpell(caster, ability, target);

		return new AccumulatedCostStats(conditionArgs);
	}

	private static AccumulatedCostStats getAccumulatedCostStats(Character caster, Ability ability, Character target) {
		var costStats = newAccumulatedCostStats(caster, ability, target);

		accumulateEffects(caster, costStats);
		return costStats;
	}

	public static SpellCostSnapshot getSpellCostSnapshot(Character caster, Ability ability, Character target) {
		if (ability.getCost() == null) {
			return null;
		}

		var costStats = getAccumulatedCostStats(caster, ability, target);

		return getSpellCostSnapshot(caster, ability, costStats);
	}

	public static SpellCostSnapshot getSpellCostSnapshot(Character caster, Ability ability, AccumulatedCostStats costStats) {
		if (ability.getCost() == null) {
			return null;
		}

		var snapshot = new SpellCostSnapshot();

		var costUnreduced = getCost(caster, ability, costStats);
		var cost = (int)(costUnreduced * (1 - costStats.getCostReductionPct() / 100));
		var cooldown = getCooldown(ability, costStats);

		snapshot.setResourceType(ability.getCost().resourceType());
		snapshot.setCost(cost);
		snapshot.setCostUnreduced(costUnreduced);
		snapshot.setCooldown(cooldown);

		return snapshot;
	}

	private static int getCost(Character caster, Ability ability, AccumulatedCostStats costStats) {
		var cost = ability.getCost();
		var result = (double) cost.amount();

		if (!cost.baseStatPct().isZero()) {
			var baseStatValue = caster.getBaseStatValue(cost.resourceType());
			var pct = cost.baseStatPct().getCoefficient();

			result += baseStatValue * pct;
		}

		if (!cost.coefficient().value().isZero()) {
			var coeff = cost.coefficient().value().getCoefficient();
			var power = costStats.getPower();

			result += power * coeff;
		}

		return (int) addAndMultiplyByPct(
				result,
				costStats.getCost(cost.resourceType()),
				costStats.getCostPct(cost.resourceType())
		);
	}

	private static double getCooldown(Ability ability, AccumulatedCostStats costStats) {
		var spellCooldown = ability.getCooldown().getSeconds();
		var cooldown = costStats.getCooldown();
		var cooldownPct = costStats.getCooldownPct();

		return addAndMultiplyByPct(spellCooldown, cooldown, cooldownPct);
	}
}
