package wow.commons.model.item;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.config.Restriction;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterInfo;

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

	public boolean canBeEquippedBy(CharacterInfo characterInfo, Phase phase) {
		return restriction.isMetBy(characterInfo, phase);
	}
}
