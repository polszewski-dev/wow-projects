package wow.commons.model.item;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.TimeRestricted;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-01-23
 */
public interface ItemSet extends TimeRestricted, CharacterRestricted {
	String getName();

	Tier getTier();

	List<ItemSetBonus> getItemSetBonuses();

	List<Item> getPieces();
}
