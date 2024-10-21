package wow.scraper.parser.spell.ability;

import wow.commons.model.Percent;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.ClassAbility;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.impl.ClassAbilityImpl;
import wow.scraper.parser.spell.SpellMatcher;
import wow.scraper.parser.spell.params.SpellPatternParams;

/**
 * User: POlszewski
 * Date: 2023-09-19
 */
public class AbilityMatcher extends SpellMatcher<AbilityPattern, SpellPatternParams, AbilityMatcherParams> {
	public AbilityMatcher(AbilityPattern pattern) {
		super(pattern);
	}

	public ClassAbility getAbility() {
		var ability = getSpell(getPatternParams(), ClassAbilityImpl::new);
		ability.setCost(getCost());
		return ability;
	}

	public AbilityCategory getAbilityCategory() {
		return getPatternParams().abilityCategory();
	}

	private Cost getCost() {
		var costParams = getPatternParams().cost();

		if (costParams == null) {
			return null;
		}

		var resourceType = costParams.type();
		var amount = getOptionalInteger(costParams.amount()).orElse(0);
		var coefficient = costParams.coefficient();

		return new Cost(resourceType, amount, Percent.ZERO, coefficient, null);
	}
}
