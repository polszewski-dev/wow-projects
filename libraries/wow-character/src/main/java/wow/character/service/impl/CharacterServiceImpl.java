package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.model.equipment.EquippableItem;
import wow.character.repository.BaseStatInfoRepository;
import wow.character.repository.CharacterTemplateRepository;
import wow.character.repository.CombatRatingInfoRepository;
import wow.character.repository.GearSetRepository;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.model.buff.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.Gem;
import wow.commons.model.profession.Profession;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.Talent;
import wow.commons.repository.item.ConsumableRepository;
import wow.commons.repository.pve.FactionRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.repository.spell.TalentRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
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
	private final ConsumableRepository consumableRepository;
	private final BaseStatInfoRepository baseStatInfoRepository;
	private final CombatRatingInfoRepository combatRatingInfoRepository;
	private final CharacterTemplateRepository characterTemplateRepository;
	private final GearSetRepository gearSetRepository;
	private final FactionRepository factionRepository;

	@Override
	public PlayerCharacter createPlayerCharacter(String name, CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId) {
		return createPlayerCharacter(name, characterClassId, raceId, level, phaseId, PlayerCharacterImpl::new);
	}

	@Override
	public <T extends PlayerCharacter> T createPlayerCharacter(
			String name, CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId, PlayerCharacterFactory<T> factory
	) {
		var phase = phaseRepository.getPhase(phaseId).orElseThrow();
		var gameVersion = phase.getGameVersion();
		var characterClass = gameVersion.getCharacterClass(characterClassId).orElseThrow();
		var race = gameVersion.getRace(raceId).orElseThrow();
		var baseStatInfo = baseStatInfoRepository.getBaseStatInfo(gameVersion.getGameVersionId(), characterClassId, raceId, level).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(gameVersion.getGameVersionId(), level).orElseThrow();
		var talents = new Talents(characterClassId, phaseId, getAvailableTalents(characterClassId, phaseId));
		var professions = new CharacterProfessions(getAvailableProfessions(phase), phase, level);
		var exclusiveFactions = new ExclusiveFactions(getAvailableExclusiveFactions(phaseId));

		return factory.newPlayerCharacter(
				name,
				phase,
				characterClass,
				race,
				level,
				baseStatInfo,
				combatRatingInfo,
				talents,
				professions,
				exclusiveFactions
		);
	}

	@Override
	public NonPlayerCharacter createNonPlayerCharacter(String name, CreatureType creatureType, int level, PhaseId phaseId) {
		return createNonPlayerCharacter(name, creatureType, level, phaseId, NonPlayerCharacterImpl::new);
	}

	@Override
	public <T extends NonPlayerCharacter> T createNonPlayerCharacter(
			String name, CreatureType creatureType, int level, PhaseId phaseId, NonPlayerCharacterFactory<T> factory
	) {
		var phase = phaseRepository.getPhase(phaseId).orElseThrow();
		var gameVersion = phase.getGameVersion();
		var characterClassId = CharacterClassId.WARRIOR;
		var characterClass = gameVersion.getCharacterClass(characterClassId).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(gameVersion.getGameVersionId(), level).orElseThrow();

		return factory.newPlayerCharacter(
				name,
				phase,
				characterClass,
				creatureType,
				level,
				combatRatingInfo
		);
	}

	@Override
	public void applyDefaultCharacterTemplate(PlayerCharacter character) {
		var characterTemplate = characterTemplateRepository.getDefaultCharacterTemplate(character).orElseThrow();

		applyCharacterTemplate(character, characterTemplate);
	}

	@Override
	public void applyCharacterTemplate(PlayerCharacter character, String templateName) {
		var characterTemplate = characterTemplateRepository.getCharacterTemplate(templateName, character).orElseThrow();

		applyCharacterTemplate(character, characterTemplate);
	}

	private void applyCharacterTemplate(PlayerCharacter character, CharacterTemplate characterTemplate) {
		changeBuild(character, characterTemplate);

		character.setProfessionMaxLevels(characterTemplate.getProfessions());
		character.getExclusiveFactions().set(characterTemplate.getExclusiveFactions());
		character.getBuffs().setHighestRanks(characterTemplate.getDefaultBuffs());

		if (character.getTarget() != null) {
			character.getTarget().getBuffs().setHighestRanks(characterTemplate.getDefaultDebuffs());
		}

		character.getConsumables().setConsumableNames(characterTemplate.getConsumables());

		updateAfterRestrictionChange(character);
	}

	private void changeBuild(PlayerCharacter character, CharacterTemplate characterTemplate) {
		var build = character.getBuild();

		build.reset();
		build.getTalents().loadFromTalentLink(characterTemplate.getTalentLink());
		build.setRole(characterTemplate.getRequiredRole());
		build.setActivePet(characterTemplate.getActivePet());
		build.setScript(characterTemplate.getDefaultScript());

		refreshSpellbook(character);
		refreshBuffs(character);
		refreshConsumables(character);
	}

	@Override
	public void updateAfterRestrictionChange(PlayerCharacter character) {
		character.getBuild().invalidate();
		refreshSpellbook(character);
		refreshActivePet(character);
		refreshEquipment(character);
		refreshBuffs(character);
		refreshConsumables(character);
	}

	private void refreshSpellbook(PlayerCharacter character) {
		character.getSpellbook().reset();
		character.getSpellbook().addAbilities(getAvailableAbilities(character));
	}

	private void refreshActivePet(PlayerCharacter character) {
		var activePet = character.getActivePet();

		if (activePet != null && !activePet.isAvailableTo(character)) {
			character.getBuild().setActivePet(null);
		}
	}

	private void refreshBuffs(PlayerCharacter character) {
		var buffs = getAvailableBuffs(character, CHARACTER_BUFF);

		character.getBuffs().setAvailable(buffs);

		if (character.getTarget() != null) {
			var debuffs = getAvailableBuffs(character, TARGET_DEBUFF);

			character.getTarget().getBuffs().setAvailable(debuffs);
		}
	}

	private void refreshConsumables(PlayerCharacter character) {
		var consumables = getAvailableConsumes(character);

		character.getConsumables().setAvailable(consumables);
	}

	private void refreshEquipment(PlayerCharacter character) {
		for (var itemSlot : ItemSlot.values()) {
			removeInvalidItem(character, itemSlot);
		}
	}

	private void removeInvalidItem(PlayerCharacter character, ItemSlot itemSlot) {
		var equipment = character.getEquipment();
		var item = equipment.get(itemSlot);

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
		var enchant = item.getEnchant();

		if (enchant != null && !enchant.isAvailableTo(character)) {
			item.enchant(null);
		}
	}

	private void removeInvalidGem(PlayerCharacter character, EquippableItem item, int socketNo) {
		var gem = item.getGem(socketNo);

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

	private List<Profession> getAvailableProfessions(Phase phase) {
		return phase.getGameVersion().getProfessions();
	}

	private List<Faction> getAvailableExclusiveFactions(PhaseId phaseId) {
		return factionRepository.getAvailableExclusiveFactions(phaseId.getGameVersionId());
	}

	private List<Buff> getAvailableBuffs(Character character, BuffListType buffListType) {
		return buffRepository.getAvailableBuffs(character.getPhaseId()).stream()
				.filter(buff -> buff.isAvailableTo(character))
				.filter(buffListType.getFilter())
				.toList();
	}

	private List<Consumable> getAvailableConsumes(Character character) {
		return consumableRepository.getAvailableConsumables(character.getPhaseId()).stream()
				.filter(consumable -> consumable.isAvailableTo(character))
				.toList();
	}

	@Override
	public void equipGearSet(PlayerCharacter character, String gearSetName) {
		var gearSet = gearSetRepository.getGearSet(gearSetName, character).orElseThrow();

		gearSet.getItemsBySlot().forEach(
				(itemSlot, equippableItem) -> character.equip(equippableItem.copy(), itemSlot)
		);
	}

	@Override
	public List<GearSet> getAvailableGearSets(PlayerCharacter character) {
		return gearSetRepository.getAvailableGearSets(character);
	}

	@Override
	public void equipItem(PlayerCharacter character, ItemSlot slot, EquippableItem equippableItem) {
		removeDuplicateGem(equippableItem, character, slot);

		character.equip(equippableItem, slot);

		unequipDuplicateItem(character, slot);
		unequipDuplicateGems(character, slot);
	}

	private void removeDuplicateGem(EquippableItem itemToEquip, PlayerCharacter character, ItemSlot slot) {
		if (itemToEquip == null || itemToEquip.getSocketCount() < 2) {
			return;
		}

		var currentItem = character.getEquippedItem(slot);

		if (currentItem == null || currentItem.getItem() != itemToEquip.getItem()) {
			return;
		}

		var uniqueGems = getUniqueGems(itemToEquip);

		if (uniqueGems.isEmpty()) {
			return;
		}

		var gemCounts = uniqueGems.stream()
				.collect(groupingBy(
						Function.identity(),
						counting()
				));

		gemCounts.entrySet().stream()
				.filter(e -> e.getValue() > 1)
				.map(Map.Entry::getKey)
				.map(duplicateGem -> getDuplicateGemSocketIdx(duplicateGem, currentItem))
				.forEach(duplicateGemSocketNo -> itemToEquip.insertGem(duplicateGemSocketNo, null));
	}

	private int getDuplicateGemSocketIdx(Gem duplicateGem, EquippableItem currentItem) {
		return IntStream.range(0, currentItem.getSocketCount())
				.filter(socketNo -> currentItem.getGem(socketNo) == duplicateGem)
				.findFirst()
				.orElseThrow();
	}

	private void unequipDuplicateItem(PlayerCharacter character, ItemSlot slot) {
		var otherSlot = getOtherSlot(slot);

		if (otherSlot == null) {
			return;
		}

		var item = character.getEquippedItem(slot);
		var otherItem = character.getEquippedItem(otherSlot);

		if (item == null || otherItem == null) {
			return;
		}

		if (item.getItem() == otherItem.getItem() && item.isUnique()) {
			character.equip(null, otherSlot);
		}
	}

	private void unequipDuplicateGems(PlayerCharacter character, ItemSlot slot) {
		var item = character.getEquippedItem(slot);

		if (item == null) {
			return;
		}

		var uniqueGems = getUniqueGems(item);

		if (uniqueGems.isEmpty()) {
			return;
		}

		for (var equippableItem : character.getEquipment().toList()) {
			for (var uniqueGem : uniqueGems) {
				if (equippableItem != item) {
					unequipDuplicateGem(equippableItem, uniqueGem);
				}
			}
		}
	}

	private List<Gem> getUniqueGems(EquippableItem item) {
		return item.getGems().stream()
				.filter(Objects::nonNull)
				.filter(Gem::isUnique)
				.toList();
	}

	private void unequipDuplicateGem(EquippableItem equippableItem, Gem uniqueGem) {
		for (int socketNo = 0; socketNo < equippableItem.getSocketCount(); ++socketNo) {
			var equippedGem = equippableItem.getGem(socketNo);

			if (equippedGem == uniqueGem) {
				equippableItem.insertGem(socketNo, null);
			}
		}
	}

	private ItemSlot getOtherSlot(ItemSlot slot) {
		return switch (slot) {
			case FINGER_1 -> ItemSlot.FINGER_2;
			case FINGER_2 -> ItemSlot.FINGER_1;
			case TRINKET_1 -> ItemSlot.TRINKET_2;
			case TRINKET_2 -> ItemSlot.TRINKET_1;
			default -> null;
		};
	}
}
