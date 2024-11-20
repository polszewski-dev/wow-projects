package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Build;
import wow.character.model.build.Rotation;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.repository.BaseStatInfoRepository;
import wow.character.repository.CharacterTemplateRepository;
import wow.character.repository.CombatRatingInfoRepository;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.model.buff.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Pet;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.Talent;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.repository.spell.TalentRepository;

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
	private final PhaseRepository phaseRepository;
	private final SpellRepository spellRepository;
	private final TalentRepository talentRepository;
	private final BuffRepository buffRepository;
	private final BaseStatInfoRepository baseStatInfoRepository;
	private final CombatRatingInfoRepository combatRatingInfoRepository;
	private final CharacterTemplateRepository characterTemplateRepository;

	@Override
	public PlayerCharacter createPlayerCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId) {
		return createPlayerCharacter(characterClassId, raceId, level, phaseId, PlayerCharacterImpl::new);
	}

	@Override
	public <T extends PlayerCharacter> T createPlayerCharacter(
			CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId, PlayerCharacterFactory<T> factory
	) {
		var phase = phaseRepository.getPhase(phaseId).orElseThrow();
		var gameVersion = phase.getGameVersion();
		var characterClass = gameVersion.getCharacterClass(characterClassId).orElseThrow();
		var race = gameVersion.getRace(raceId).orElseThrow();
		var baseStatInfo = baseStatInfoRepository.getBaseStatInfo(gameVersion.getGameVersionId(), characterClassId, raceId, level).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(gameVersion.getGameVersionId(), level).orElseThrow();
		var talents = new Talents(getAvailableTalents(characterClassId, phaseId));

		return factory.newPlayerCharacter(
				phase,
				characterClass,
				race,
				level,
				baseStatInfo,
				combatRatingInfo,
				talents
		);
	}

	@Override
	public NonPlayerCharacter createNonPlayerCharacter(CreatureType creatureType, int level, PhaseId phaseId) {
		return createNonPlayerCharacter(creatureType, level, phaseId, NonPlayerCharacterImpl::new);
	}

	@Override
	public <T extends NonPlayerCharacter> T createNonPlayerCharacter(
			CreatureType creatureType, int level, PhaseId phaseId, NonPlayerCharacterFactory<T> factory
	) {
		var phase = phaseRepository.getPhase(phaseId).orElseThrow();
		var gameVersion = phase.getGameVersion();
		var characterClassId = CharacterClassId.WARRIOR;
		var characterClass = gameVersion.getCharacterClass(characterClassId).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(gameVersion.getGameVersionId(), level).orElseThrow();

		return factory.newPlayerCharacter(
				phase,
				characterClass,
				creatureType,
				level,
				combatRatingInfo
		);
	}

	@Override
	public void applyDefaultCharacterTemplate(PlayerCharacter character) {
		var characterTemplate = characterTemplateRepository.getDefaultCharacterTemplate(
				character.getCharacterClassId(),
				character.getLevel(),
				character.getPhaseId()
		).orElseThrow();

		applyCharacterTemplate(character, characterTemplate);
	}

	@Override
	public void applyCharacterTemplate(PlayerCharacter character, CharacterTemplateId characterTemplateId) {
		var characterTemplate = characterTemplateRepository.getCharacterTemplate(
				characterTemplateId,
				character.getCharacterClassId(),
				character.getLevel(),
				character.getPhaseId()
		).orElseThrow();

		applyCharacterTemplate(character, characterTemplate);
	}

	private void applyCharacterTemplate(PlayerCharacter character, CharacterTemplate characterTemplate) {
		changeBuild(character, characterTemplate);

		character.setProfessions(getMaxedProfessions(characterTemplate, character));
		character.getExclusiveFactions().set(characterTemplate.getExclusiveFactions());
		character.getBuffs().setHighestRanks(characterTemplate.getDefaultBuffs());

		if (character.getTarget() != null) {
			character.getTarget().getBuffs().setHighestRanks(characterTemplate.getDefaultDebuffs());
		}

		updateAfterRestrictionChange(character);
	}

	private List<CharacterProfession> getMaxedProfessions(CharacterTemplate characterTemplate, PlayerCharacter character) {
		return characterTemplate.getProfessions().stream()
				.map(x -> getCharacterProfessionMaxLevel(x, character))
				.toList();
	}

	private CharacterProfession getCharacterProfessionMaxLevel(CharacterProfession characterProfession, PlayerCharacter character) {
		return CharacterProfession.getCharacterProfessionMaxLevel(
				character.getPhase(),
				characterProfession.getProfessionId(),
				characterProfession.getSpecializationId(),
				character.getLevel()
		);
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
		character.getSpellbook().addAbilities(getAvailableAbilities(character));
	}

	private void refreshActivePet(PlayerCharacter character) {
		Pet activePet = character.getActivePet();

		if (activePet != null && !activePet.isAvailableTo(character)) {
			character.getBuild().setActivePet(null);
		}
	}

	private void refreshBuffs(PlayerCharacter character) {
		List<Buff> buffs = getAvailableBuffs(character, CHARACTER_BUFF);

		character.getBuffs().setAvailable(buffs);

		if (character.getTarget() != null) {
			List<Buff> debuffs = getAvailableBuffs(character, TARGET_DEBUFF);

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

	private List<Ability> getAvailableAbilities(Character character) {
		return spellRepository.getAvailableAbilities(character.getCharacterClassId(), character.getLevel(), character.getPhaseId()).stream()
				.filter(spell -> spell.isAvailableTo(character))
				.toList();
	}

	private List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentRepository.getAvailableTalents(characterClassId, phaseId);
	}

	private List<Buff> getAvailableBuffs(Character character, BuffListType buffListType) {
		return buffRepository.getAvailableBuffs(character.getPhaseId()).stream()
				.filter(buff -> buff.isAvailableTo(character))
				.filter(buffListType.getFilter())
				.toList();
	}
}
