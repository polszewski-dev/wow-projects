package wow.simulator.model.context;

import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-15
 */
public class SpellResolutionConversions extends Conversions {
	public SpellResolutionConversions(Unit caster, Ability ability) {
		super(caster, ability);
		addAbilityConversion();
	}
}
