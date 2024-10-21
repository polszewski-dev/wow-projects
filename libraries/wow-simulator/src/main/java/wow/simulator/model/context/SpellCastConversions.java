package wow.simulator.model.context;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Conversion;
import wow.commons.model.spell.Cost;
import wow.simulator.model.unit.Unit;

import static wow.commons.model.spell.Conversion.From.HEALTH_PAID;
import static wow.commons.model.spell.Conversion.From.MANA_PAID;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class SpellCastConversions extends Conversions {
	public SpellCastConversions(Unit caster, Ability ability) {
		super(caster, ability);
		addAbilityConversion();
	}

	public void performPaidCostConversion(Cost cost) {
		var from = getFrom(cost);
		var amount = cost.amount();
		performConversions(from, amount);
	}

	private Conversion.From getFrom(Cost cost) {
		return switch (cost.resourceType()) {
			case HEALTH -> HEALTH_PAID;
			case MANA -> MANA_PAID;
			case PET_MANA, ENERGY, RAGE -> throw new UnsupportedOperationException();
		};
	}
}
