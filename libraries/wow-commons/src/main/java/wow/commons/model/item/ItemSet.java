package wow.commons.model.item;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.profession.ProfessionId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-01-23
 */
public interface ItemSet extends Described, TimeRestricted, CharacterRestricted {
	List<ItemSetBonus> getItemSetBonuses();

	List<String> getPieces();

	ProfessionId getRequiredProfession();
}
