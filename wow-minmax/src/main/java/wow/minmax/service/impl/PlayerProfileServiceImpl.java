package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.*;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.repository.CharacterRepository;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.SpellDataRepository;
import wow.commons.util.TalentCalculatorUtil;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.persistent.BuffPOConverter;
import wow.minmax.converter.persistent.CharacterProfessionPOConverter;
import wow.minmax.converter.persistent.EquipmentPOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final UpgradeService upgradeService;
	private final PlayerProfileRepository playerProfileRepository;
	private final ItemDataRepository itemDataRepository;
	private final SpellDataRepository spellDataRepository;
	private final CharacterRepository characterRepository;
	private final ProfileConfig profileConfig;

	private final EquipmentPOConverter equipmentPOConverter;
	private final CharacterProfessionPOConverter characterProfessionPOConverter;
	private final BuffPOConverter buffPOConverter;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return playerProfileRepository.getPlayerProfileList().stream()
				.filter(profile -> profile.getPhase().getGameVersion() == profileConfig.getGameVersion())
				.map(this::getPlayerProfile)
				.collect(Collectors.toList());
	}

	@Override
	public PlayerProfile createPlayerProfile(String profileName, Phase phase) {
		PlayerProfile playerProfile = createTemporaryPlayerProfile(
				UUID.randomUUID(),
				profileName,
				profileConfig.getDefaultClass(),
				profileConfig.getDefaultRace(),
				phase.getGameVersion().getMaxLevel(),
				profileConfig.getDefaultBuild(),
				profileConfig.getDefaultProfessions(phase), profileConfig.getDefaultEnemyType(),
				phase
		);
		
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile createTemporaryPlayerProfile(
			UUID profileId, String profileName, CharacterClass characterClass, Race race, int level, BuildId buildId, List<CharacterProfession> professions, CreatureType enemyType, Phase phase
	) {
		CharacterInfo characterInfo = createCharacterInfo(characterClass, race, level, buildId, professions, phase);

		PlayerProfile playerProfile = new PlayerProfile(
				profileId,
				profileName,
				characterInfo,
				enemyType
		);

		playerProfile.setBuffs(BuffSetId.values());

		return playerProfile;
	}

	private CharacterInfo createCharacterInfo(CharacterClass characterClass, Race race, int level, BuildId buildId, List<CharacterProfession> professions, Phase phase) {
		CharacterInfo characterInfo = new CharacterInfo(
				characterClass,
				race,
				level,
				Build.EMPTY,
				new CharacterProfessions(professions),
				phase
		);

		Build build = createBuild(buildId, characterInfo);

		return characterInfo.setBuild(build);
	}

	private Build createBuild(BuildId buildId, CharacterInfo characterInfo) {
		BuildTemplate buildTemplate = characterRepository.getBuildTemplate(
				buildId, characterInfo.getCharacterClass(), characterInfo.getLevel()).orElseThrow();

		Build build = new Build(
				buildId,
				buildTemplate.getTalentLink(),
				TalentCalculatorUtil.parseFromLink(buildTemplate.getTalentLink(), spellDataRepository),
				buildTemplate.getRole(),
				null,
				List.of(),
				buildTemplate.getActivePet(),
				Map.of()
		);

		characterInfo = characterInfo.setBuild(build);

		return build
				.setDamagingSpell(getAvailableSpellHighestRank(buildTemplate.getDamagingSpell(), characterInfo).orElseThrow())
				.setRelevantSpells(getAvailableSpellsHighestRanks(buildTemplate.getRelevantSpells(), characterInfo))
				.setBuffSets(getBuffSets(buildTemplate, characterInfo));
	}

	private Optional<Spell> getAvailableSpellHighestRank(SpellId spellId, CharacterInfo characterInfo) {
		return spellDataRepository.getAllSpellRanks(spellId).stream()
				.filter(spell -> spell.getRestriction().isMetBy(characterInfo))
				.max(Comparator.comparing(Spell::getRank));
	}

	private List<Spell> getAvailableSpellsHighestRanks(List<SpellId> spellIds, CharacterInfo characterInfo) {
		return spellIds.stream()
				.map(spellId -> getAvailableSpellHighestRank(spellId, characterInfo))
				.map(optionalSpell -> optionalSpell.orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private Map<BuffSetId, List<Buff>> getBuffSets(BuildTemplate buildTemplate, CharacterInfo characterInfo) {
		Map<BuffSetId, List<Buff>> result = new EnumMap<>(BuffSetId.class);
		for (var entry : buildTemplate.getBuffSets().entrySet()) {
			List<Buff> buffs = getBuffs(entry.getValue(), characterInfo);
			result.put(entry.getKey(), buffs);
		}
		return result;
	}

	private List<Buff> getBuffs(List<String> buffNames, CharacterInfo characterInfo) {
		return spellDataRepository.getBuffs(buffNames).stream()
				.filter(buff -> buff.getRestriction().isMetBy(characterInfo))
				.collect(Collectors.toList());
	}

	@Override
	public PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, Phase phase) {
		PlayerProfile copiedProfile = getPlayerProfile(copiedProfileId);
		PlayerProfile copy = copiedProfile.copy(UUID.randomUUID(), profileName, phase);

		saveProfile(copy);
		return copy;
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return getPlayerProfile(playerProfileRepository.getPlayerProfile(profileId).orElseThrow());
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Item item = itemDataRepository.getItem(itemId).orElseThrow();
		EquippableItem bestItemVariant = upgradeService.getBestItemVariant(playerProfile.copy(), item, slot, playerProfile.getDamagingSpell());

		playerProfile.getEquipment().set(bestItemVariant, slot);
		saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Enchant enchant = itemDataRepository.getEnchant(enchantId).orElseThrow();

		playerProfile.getEquipment().get(slot).enchant(enchant);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Gem gem = itemDataRepository.getGem(gemId).orElseThrow();

		playerProfile.getEquipment().get(slot).insertGem(socketNo, gem);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile resetEquipment(UUID profileId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);

		playerProfile.setEquipment(new Equipment());
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Buff buff = spellDataRepository.getBuff(buffId).orElseThrow();

		playerProfile.enableBuff(buff, enabled);
		saveProfile(playerProfile);
		return playerProfile;
	}

	private void saveProfile(PlayerProfile playerProfile) {
		PlayerProfilePO playerProfilePO = getPlayerProfilePO(playerProfile);

		playerProfileRepository.saveProfile(playerProfilePO);
	}

	private PlayerProfilePO getPlayerProfilePO(PlayerProfile playerProfile) {
		return new PlayerProfilePO(
				playerProfile.getProfileId(),
				playerProfile.getProfileName(),
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getBuild().getBuildId(),
				characterProfessionPOConverter.convertList(playerProfile.getProfessions()),
				playerProfile.getEnemyType(),
				playerProfile.getPhase(),
				equipmentPOConverter.convert(playerProfile.getEquipment()),
				buffPOConverter.convertList(playerProfile.getBuffs()),
				playerProfile.getLastModified()
		);
	}

	private PlayerProfile getPlayerProfile(PlayerProfilePO value) {
		PlayerProfile playerProfile = createTemporaryPlayerProfile(
				value.getProfileId(),
				value.getProfileName(),
				value.getCharacterClass(),
				value.getRace(),
				value.getLevel(),
				value.getBuildId(),
				characterProfessionPOConverter.convertBackList(value.getProfessions()),
				value.getEnemyType(),
				value.getPhase()
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(value.getEquipment()));
		playerProfile.setBuffs(buffPOConverter.convertBackList(value.getBuffs()));
		playerProfile.setLastModified(value.getLastModified());
		return playerProfile;
	}
}
