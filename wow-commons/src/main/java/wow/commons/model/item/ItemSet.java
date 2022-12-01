package wow.commons.model.item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.config.Restriction;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-01-23
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "name")
public class ItemSet {
	private final String name;
	private final Tier tier;
	private final Restriction restriction;
	private final List<ItemSetBonus> itemSetBonuses;
	private final List<Item> pieces;

	public boolean canBeEquippedBy(CharacterInfo characterInfo) {
		return restriction.isMetBy(characterInfo);
	}
}
