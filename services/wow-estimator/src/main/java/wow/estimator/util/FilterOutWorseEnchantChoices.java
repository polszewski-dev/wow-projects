package wow.estimator.util;

import wow.commons.model.effect.Effect;
import wow.commons.model.item.Enchant;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public class FilterOutWorseEnchantChoices extends FilterOutWorseChoices<Enchant> {
	public FilterOutWorseEnchantChoices(List<Enchant> enchants) {
		super(enchants);
	}

	@Override
	protected List<Effect> getEffects(Enchant choice) {
		return List.of(choice.getEffect());
	}

	@Override
	protected int getOrderWithinTheSameStatTotal(Enchant enchant) {
		return 0;
	}

	@Override
	protected Object getGroupKey(Enchant enchant) {
		return "";
	}
}
