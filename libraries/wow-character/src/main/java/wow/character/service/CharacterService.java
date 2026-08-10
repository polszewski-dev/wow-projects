package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Spell;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	PlayerCharacter createPlayerCharacter(String name, CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	<T extends PlayerCharacter> T createPlayerCharacter(String name, CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId, PlayerCharacterFactory<T> factory);

	NonPlayerCharacter createNonPlayerCharacter(String name, CreatureType creatureType, int level, PhaseId phaseId);

	<T extends NonPlayerCharacter> T createNonPlayerCharacter(String name, CreatureType creatureType, int level, PhaseId phaseId, NonPlayerCharacterFactory<T> factory);

	<T extends PetCharacter> T createPetCharacter(String name, PetType petType, Character master, Spell sourceSpell, PetCharacterFactory<T> factory);

	void applyDefaultCharacterTemplate(PlayerCharacter player);

	void applyCharacterTemplate(PlayerCharacter player, String templateName);

	void updateAfterRestrictionChange(PlayerCharacter player);

	void equipGearSet(PlayerCharacter player, String gearSetName);

	List<GearSet> getAvailableGearSets(PlayerCharacter player);

	void equipItem(PlayerCharacter player, ItemSlot slot, EquippableItem equippableItem);

	void equipItemGroup(PlayerCharacter player, ItemSlotGroup slotGroup, List<EquippableItem> items);
}
