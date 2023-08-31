package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Build;
import wow.character.model.build.Rotation;
import wow.character.model.build.Talents;
import wow.character.model.character.*;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;
import wow.character.service.SpellService;
import wow.commons.model.buff.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.PhaseId;

import java.util.List;

import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.character.model.character.BuffListType.TARGET_DEBUFF;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
@Service
@AllArgsConstructor
public class CharacterServiceImpl implements CharacterService {
	private final CharacterRepository characterRepository;

	private final SpellService spellService;

	@Override
	public PlayerCharacter createPlayerCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId) {
		Phase phase = characterRepository.getPhase(phaseId).orElseThrow();
		GameVersion gameVersion = phase.getGameVersion();

		return new PlayerCharacterImpl(
				phase,
				gameVersion.getCharacterClass(characterClassId),
				gameVersion.getRace(raceId),
				level,
				new Talents(spellService.getAvailableTalents(characterClassId, phaseId))
		);
	}

	@Override
	public NonPlayerCharacter createNonPlayerCharacter(CreatureType creatureType, int level, PhaseId phaseId) {
		Phase phase = characterRepository.getPhase(phaseId).orElseThrow();
		GameVersion gameVersion = phase.getGameVersion();
		CharacterClassId characterClassId = CharacterClassId.WARRIOR;

		return new NonPlayerCharacterImpl(
				phase,
				gameVersion.getCharacterClass(characterClassId),
				creatureType,
				level
		);
	}

	@Override
	public void applyCharacterTemplate(PlayerCharacter character, CharacterTemplateId characterTemplateId) {
		CharacterTemplate characterTemplate = getCharacterTemplate(characterTemplateId, character);

		changeBuild(character, characterTemplate);

		character.setProfessions(characterTemplate.getProfessions());
		character.getExclusiveFactions().set(characterTemplate.getExclusiveFactions());
		character.getBuffs().setHighestRanks(characterTemplate.getDefaultBuffs());
		if (character.getTarget() != null) {
			character.getTarget().getBuffs().setHighestRanks(characterTemplate.getDefaultDebuffs());
		}

		updateAfterRestrictionChange(character);
	}

	private CharacterTemplate getCharacterTemplate(CharacterTemplateId characterTemplateId, PlayerCharacter character) {
		return characterRepository.getCharacterTemplate(
				characterTemplateId, character.getCharacterClassId(), character.getLevel(), character.getPhaseId()
		).orElseThrow();
	}

	private void changeBuild(PlayerCharacter character, CharacterTemplate characterTemplate) {
		Build build = character.getBuild();

		build.reset();
		build.getTalents().loadFromTalentLink(characterTemplate.getTalentLink());
		build.setRole(characterTemplate.getRole());
		build.setActivePet(characterTemplate.getActivePet());
		build.setRotation(characterTemplate.getDefaultRotationTemplate().createRotation());

		refreshSpellbook(character);
		refreshBuffs(character);
	}

	@Override
	public void updateAfterRestrictionChange(PlayerCharacter character) {
		Rotation rotation = character.getBuild().getRotation();

		if (rotation != null) {
			rotation.invalidate();
		}

		refreshSpellbook(character);
		refreshActivePet(character);
		refreshEquipment(character);
		refreshBuffs(character);
	}

	private void refreshSpellbook(PlayerCharacter character) {
		character.getSpellbook().reset();
		character.getSpellbook().addAbilities(spellService.getAvailableAbilities(character));
	}

	private void refreshActivePet(PlayerCharacter character) {
		Pet activePet = character.getActivePet();

		if (activePet != null && !activePet.isAvailableTo(character)) {
			character.getBuild().setActivePet(null);
		}
	}

	private void refreshBuffs(PlayerCharacter character) {
		List<Buff> buffs = spellService.getAvailableBuffs(character, CHARACTER_BUFF);
		List<Buff> debuffs = spellService.getAvailableBuffs(character, TARGET_DEBUFF);

		character.getBuffs().setAvailable(buffs);
		if (character.getTarget() != null) {
			character.getTarget().getBuffs().setAvailable(debuffs);
		}
	}

	private void refreshEquipment(PlayerCharacter character) {
		for (ItemSlot itemSlot : ItemSlot.values()) {
			removeInvalidItem(character, itemSlot);
		}
	}

	private void removeInvalidItem(PlayerCharacter character, ItemSlot itemSlot) {
		Equipment equipment = character.getEquipment();
		EquippableItem item = equipment.get(itemSlot);

		if (item == null) {
			return;
		}

		if (!item.getItem().isAvailableTo(character)) {
			equipment.equip(null, itemSlot);
			return;
		}

		removeInvalidEnchant(character, item);

		for (int socketNo = 0; socketNo < item.getSocketCount(); ++socketNo) {
			removeInvalidGem(character, item, socketNo);
		}
	}

	private void removeInvalidEnchant(PlayerCharacter character, EquippableItem item) {
		Enchant enchant = item.getEnchant();

		if (enchant != null && !enchant.isAvailableTo(character)) {
			item.enchant(null);
		}
	}

	private void removeInvalidGem(PlayerCharacter character, EquippableItem item, int socketNo) {
		Gem gem = item.getGem(socketNo);

		if (gem != null && !gem.isAvailableTo(character)) {
			item.getSockets().insertGem(socketNo, null);
		}
	}
}
