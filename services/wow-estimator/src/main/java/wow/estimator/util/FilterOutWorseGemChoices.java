package wow.estimator.util;

import wow.commons.model.effect.Effect;
import wow.commons.model.item.Gem;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public class FilterOutWorseGemChoices extends FilterOutWorseChoices<Gem> {
	public FilterOutWorseGemChoices(List<Gem> gems) {
		super(gems);
	}

	@Override
	protected List<Effect> getEffects(Gem choice) {
		return choice.getEffects();
	}

	@Override
	protected int getOrderWithinTheSameStatTotal(Gem gem) {
		if (gem.isCrafted() && !gem.isUnique()) {
			return 1;
		}
		if (gem.isCrafted()) {
			return 2;
		}
		if(gem.isPvPReward()) {
			return 3;
		}
		return 4;
	}

	@Override
	protected Object getGroupKey(Gem gem) {
		return gem.getColor();
	}
}
