package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Talents;
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
import wow.commons.model.categorization.ItemSlotGroup;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.Math.min;
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
	public void applyDefaultCharacterTemplate(PlayerCharacter player) {
		var characterTemplate = characterTemplateRepository.getDefaultCharacterTemplate(player).orElseThrow();

		applyCharacterTemplate(player, characterTemplate);
	}

	@Override
	public void applyCharacterTemplate(PlayerCharacter player, String templateName) {
		var characterTemplate = characterTemplateRepository.getCharacterTemplate(templateName, player).orElseThrow();

		applyCharacterTemplate(player, characterTemplate);
	}

	private void applyCharacterTemplate(PlayerCharacter player, CharacterTemplate characterTemplate) {
		changeBuild(player, characterTemplate);

		player.setProfessionMaxLevels(characterTemplate.getProfessions());
		player.getExclusiveFactions().set(characterTemplate.getExclusiveFactions());
		player.getBuffs().setNames(characterTemplate.getDefaultBuffs());

		if (player.getTarget() != null) {
			player.getTarget().getBuffs().setNames(characterTemplate.getDefaultDebuffs());
		}

		player.getConsumables().setNames(characterTemplate.getConsumables());

		updateAfterRestrictionChange(player);
	}

	private void changeBuild(PlayerCharacter player, CharacterTemplate characterTemplate) {
		var build = player.getBuild();

		build.reset();
		build.getTalents().loadFromTalentLink(characterTemplate.getTalentLink());
		build.setRole(characterTemplate.getRequiredRole());
		build.setActivePet(characterTemplate.getActivePet());
		build.setScript(characterTemplate.getDefaultScript());

		refreshSpellbook(player);
		refreshBuffs(player);
		refreshConsumables(player);
	}

	@Override
	public void updateAfterRestrictionChange(PlayerCharacter player) {
		player.getBuild().invalidate();
		refreshSpellbook(player);
		refreshActivePet(player);
		refreshEquipment(player);
		refreshBuffs(player);
		refreshConsumables(player);
	}

	private void refreshSpellbook(PlayerCharacter player) {
		player.getSpellbook().reset();
		player.getSpellbook().addAbilities(getAvailableAbilities(player));
	}

	private void refreshActivePet(PlayerCharacter player) {
		var activePet = player.getActivePet();

		if (activePet != null && !activePet.isAvailableTo(player)) {
			player.getBuild().setActivePet(null);
		}
	}

	private void refreshBuffs(PlayerCharacter player) {
		var buffs = getAvailableBuffs(player, CHARACTER_BUFF);

		player.getBuffs().setAvailable(buffs);

		if (player.getTarget() != null) {
			var debuffs = getAvailableBuffs(player, TARGET_DEBUFF);

			player.getTarget().getBuffs().setAvailable(debuffs);
		}
	}

	private void refreshConsumables(PlayerCharacter player) {
		var consumables = getAvailableConsumes(player);

		player.getConsumables().setAvailable(consumables);
	}

	private void refreshEquipment(PlayerCharacter player) {
		for (var itemSlot : ItemSlot.values()) {
			removeInvalidItem(player, itemSlot);
		}
	}

	private void removeInvalidItem(PlayerCharacter player, ItemSlot itemSlot) {
		var equipment = player.getEquipment();
		var item = equipment.get(itemSlot);

		if (item == null) {
			return;
		}

		if (!item.getItem().isAvailableTo(player)) {
			equipment.equip(null, itemSlot);
			return;
		}

		removeInvalidEnchant(player, item);

		for (int socketNo = 0; socketNo < item.getSocketCount(); ++socketNo) {
			removeInvalidGem(player, item, socketNo);
		}
	}

	private void removeInvalidEnchant(PlayerCharacter player, EquippableItem item) {
		var enchant = item.getEnchant();

		if (enchant != null && !enchant.isAvailableTo(player)) {
			item.enchant(null);
		}
	}

	private void removeInvalidGem(PlayerCharacter player, EquippableItem item, int socketNo) {
		var gem = item.getGem(socketNo);

		if (gem != null && !gem.isAvailableTo(player)) {
			item.getSockets().insertGem(socketNo, null);
		}
	}

	private List<Ability> getAvailableAbilities(PlayerCharacter player) {
		return spellRepository.getAvailableAbilities(player.getCharacterClassId(), player.getLevel(), player.getPhaseId()).stream()
				.filter(spell -> spell.isAvailableTo(player))
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

	private List<Buff> getAvailableBuffs(PlayerCharacter player, BuffListType buffListType) {
		return buffRepository.getAvailableBuffs(
				player.getPhaseId(),
				buff -> buff.isAvailableTo(player) && buffListType.getFilter().test(buff)
		);
	}

	private List<Consumable> getAvailableConsumes(PlayerCharacter player) {
		return consumableRepository.getAvailableConsumables(player.getPhaseId()).stream()
				.filter(consumable -> consumable.isAvailableTo(player))
				.toList();
	}

	@Override
	public void equipGearSet(PlayerCharacter player, String gearSetName) {
		var gearSet = gearSetRepository.getGearSet(gearSetName, player).orElseThrow();

		gearSet.getItemsBySlot().forEach(
				(itemSlot, equippableItem) -> player.equip(equippableItem.copy(), itemSlot)
		);
	}

	@Override
	public List<GearSet> getAvailableGearSets(PlayerCharacter player) {
		return gearSetRepository.getAvailableGearSets(player);
	}

	@Override
	public void equipItem(PlayerCharacter player, ItemSlot slot, EquippableItem equippableItem) {
		removeDuplicateGem(equippableItem, player, slot);

		player.equip(equippableItem, slot);

		unequipDuplicateItem(player, slot);
		unequipDuplicateGems(player, slot);
	}

	@Override
	public void equipItemGroup(PlayerCharacter player, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		var slots = slotGroup.getSlots();

		for (var slot : slots) {
			player.equip(null, slot);
		}

		if (slotGroup == ItemSlotGroup.WEAPONS && items.size() == 1) {
			items = Arrays.asList(items.getFirst(), null);
		}

		for (int slotIdx = 0; slotIdx < min(slots.size(), items.size()); slotIdx++) {
			var slot = slots.get(slotIdx);
			var item = items.get(slotIdx);

			equipItem(player, slot, item);
		}
	}

	private void removeDuplicateGem(EquippableItem itemToEquip, PlayerCharacter player, ItemSlot slot) {
		if (itemToEquip == null || itemToEquip.getSocketCount() < 2) {
			return;
		}

		var currentItem = player.getEquippedItem(slot);

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

	private void unequipDuplicateItem(PlayerCharacter player, ItemSlot slot) {
		var otherSlot = getOtherSlot(slot);

		if (otherSlot == null) {
			return;
		}

		var item = player.getEquippedItem(slot);
		var otherItem = player.getEquippedItem(otherSlot);

		if (item == null || otherItem == null) {
			return;
		}

		if (item.getItem() == otherItem.getItem() && item.isUnique()) {
			player.equip(null, otherSlot);
		}
	}

	private void unequipDuplicateGems(PlayerCharacter player, ItemSlot slot) {
		var item = player.getEquippedItem(slot);

		if (item == null) {
			return;
		}

		var uniqueGems = getUniqueGems(item);

		if (uniqueGems.isEmpty()) {
			return;
		}

		for (var equippableItem : player.getEquipment().toList()) {
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
