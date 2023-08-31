package wow.character.service.impl.enumerator;

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
	protected int getOrderWithinTheSameStatChoices(Gem gem) {
		if (gem.isCrafted()) {
			return 1;
		}
		if(gem.isPvPReward()) {
			return 2;
		}
		return 3;
	}

	@Override
	protected Object getGroupKey(Gem gem) {
		return gem.getColor();
	}
}
