package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.Map;

import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2025-03-26
 */
@AllArgsConstructor
@Getter
public class GearSet implements TimeRestricted, CharacterRestricted {
	private final String name;
	private final CharacterRestriction characterRestriction;
	private final TimeRestriction timeRestriction;
	private final Map<ItemSlot, EquippableItem> itemsBySlot;

	public CharacterClassId getRequiredCharacterClassId() {
		return getUniqueResult(getRequiredCharacterClassIds()).orElseThrow();
	}

	public EquippableItem getItem(ItemSlot itemSlot) {
		return itemsBySlot.get(itemSlot);
	}
}
