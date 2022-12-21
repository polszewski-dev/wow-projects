package wow.character.service.impl.enumerators;

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
	protected int getOrderWithinTheSameStatChoices(Enchant enchant) {
		return 0;
	}

	@Override
	protected Object getGroupKey(Enchant enchant) {
		return "";
	}
}
