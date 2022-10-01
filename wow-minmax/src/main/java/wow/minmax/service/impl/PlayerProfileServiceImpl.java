package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.SpellDataRepository;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.Snapshot;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final PlayerProfileRepository playerProfileRepository;
	private final ItemDataRepository itemDataRepository;
	private final SpellDataRepository spellDataRepository;

	private final UpgradeService upgradeService;
	private final CalculationService calculationService;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return playerProfileRepository.getPlayerProfileList();
	}

	@Override
	public PlayerProfile createPlayerProfile(String profileName, int phase) {
		return playerProfileRepository.createPlayerProfile(profileName, phase);
	}

	@Override
	public PlayerProfile createTemporaryPlayerProfile(String profileName, int phase) {
		return playerProfileRepository.createTemporaryPlayerProfile(profileName, phase);
	}

	@Override
	public PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, int phase) {
		return playerProfileRepository.copyPlayerProfile(copiedProfileId, profileName, phase);
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return playerProfileRepository.getPlayerProfile(profileId);
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId);
		Item item = itemDataRepository.getItem(itemId);
		EquippableItem bestItemVariant = upgradeService.getBestItemVariant(playerProfile.readOnlyCopy(), item, slot, playerProfile.getDamagingSpellId());

		playerProfile.getEquipment().set(bestItemVariant, slot);
		playerProfileRepository.saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		return playerProfileRepository.changeEnchant(profileId, slot, enchantId);
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		return playerProfileRepository.changeGem(profileId, slot, socketNo, gemId);
	}

	@Override
	public PlayerProfile resetEquipment(UUID profileId) {
		return playerProfileRepository.resetEquipment(profileId);
	}

	@Override
	public PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId);
		Buff buff = spellDataRepository.getBuff(buffId);

		List<Buff> existingBuffs = playerProfile.getBuffs();

		if (buff.getExclusionGroup() != null) {
			existingBuffs.removeIf(existingBuff -> existingBuff.getExclusionGroup() == buff.getExclusionGroup());
		}
		existingBuffs.removeIf(existingBuff -> existingBuff.getId() == buffId);

		if (enabled) {
			existingBuffs.add(buff);
		}

		playerProfile.setBuffs(existingBuffs);
		playerProfileRepository.saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public StatProvider getPlayerStatsProvider(PlayerProfile playerProfile, AttributeEvaluator attributeEvaluator) {
		return new StatProvider() {
			private Snapshot snapshot;

			@Override
			public double hitChance() {
				return getSnapshot().hitChance;
			}

			@Override
			public double critChance() {
				return getSnapshot().critChance;
			}

			@Override
			public Duration castTime() {
				return getSnapshot().effectiveCastTime;
			}

			private Snapshot getSnapshot() {
				if (snapshot == null) {
					Spell spell = playerProfile.getDamagingSpell();
					Attributes attributes = attributeEvaluator.getAttributes();
					this.snapshot = calculationService.getSnapshot(playerProfile, spell, attributes);
				}
				return snapshot;
			}
		};
	}
}
