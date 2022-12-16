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
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final PlayerProfileRepository playerProfileRepository;
	private final PlayerProfilePOConverter playerProfilePOConverter;
	private final ProfileConfig profileConfig;

	private final CharacterService characterService;
	private final ItemService itemService;
	private final SpellService spellService;
	private final UpgradeService upgradeService;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return playerProfileRepository.getPlayerProfileList().stream()
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
		EnemyInfo enemyInfo = new EnemyInfo(enemyType);

		PlayerProfile playerProfile = new PlayerProfile(
				profileId,
				profileName,
				characterInfo,
				enemyInfo
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
				phase,
				null,
				null
		);

		Build build = createBuild(buildId, characterInfo);

		return characterInfo
				.setBuild(build)
				.setBaseStatInfo(characterService.getBaseStats(characterInfo))
				.setCombatRatingInfo(characterService.getCombatRatings(characterInfo));
	}

	private Build createBuild(BuildId buildId, CharacterInfo characterInfo) {
		BuildTemplate buildTemplate = characterService.getBuildTemplate(buildId, characterInfo);

		Build build = new Build(
				buildId,
				buildTemplate.getTalentLink(),
				spellService.getTalentsFromTalentLink(buildTemplate.getTalentLink(), characterInfo),
				buildTemplate.getRole(),
				null,
				List.of(),
				buildTemplate.getActivePet(),
				Map.of()
		);

		characterInfo = characterInfo.setBuild(build);

		return build
				.setDamagingSpell(spellService.getSpellHighestRank(buildTemplate.getDamagingSpell(), characterInfo))
				.setRelevantSpells(spellService.getSpellHighestRanks(buildTemplate.getRelevantSpells(), characterInfo))
				.setBuffSets(getBuffSets(buildTemplate, characterInfo));
	}

	private Map<BuffSetId, List<Buff>> getBuffSets(BuildTemplate buildTemplate, CharacterInfo characterInfo) {
		Map<BuffSetId, List<Buff>> result = new EnumMap<>(BuffSetId.class);
		for (var entry : buildTemplate.getBuffSets().entrySet()) {
			List<Buff> buffs = spellService.getBuffs(entry.getValue(), characterInfo);
			result.put(entry.getKey(), buffs);
		}
		return result;
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
		Item item = itemService.getItem(itemId, playerProfile.getPhase());
		EquippableItem bestItemVariant = upgradeService.getBestItemVariant(playerProfile, item, slot, playerProfile.getDamagingSpell());

		playerProfile.getEquipment().set(bestItemVariant, slot);
		saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Enchant enchant = itemService.getEnchant(enchantId, playerProfile.getPhase());

		playerProfile.getEquipment().get(slot).enchant(enchant);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Gem gem = itemService.getGem(gemId, playerProfile.getPhase());

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
		Buff buff = spellService.getBuff(buffId, playerProfile.getPhase());

		playerProfile.enableBuff(buff, enabled);
		saveProfile(playerProfile);
		return playerProfile;
	}

	private void saveProfile(PlayerProfile playerProfile) {
		var converterParams = getConverterParams(playerProfile.getPhase());
		PlayerProfilePO playerProfilePO = playerProfilePOConverter.convert(playerProfile, converterParams);

		playerProfileRepository.saveProfile(playerProfilePO);
	}

	private PlayerProfile getPlayerProfile(PlayerProfilePO profile) {
		var converterParams = getConverterParams(profile.getPhase());
		return playerProfilePOConverter.convertBack(profile, converterParams);
	}

	private Map<String, Object> getConverterParams(Phase phase) {
		return Map.of(
				PlayerProfilePOConverter.PARAM_PLAYER_PROFILE_SERVICE, this,
				PlayerProfilePOConverter.PARAM_PHASE, phase
		);
	}
}
