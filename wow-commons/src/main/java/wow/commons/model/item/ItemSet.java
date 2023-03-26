package wow.commons.model.item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-01-23
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "name")
public class ItemSet implements TimeRestricted, CharacterRestricted {
	private final String name;
	private final Tier tier;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final List<ItemSetBonus> itemSetBonuses;
	private final List<Item> pieces;
}
