package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.PveRole;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.config.ItemConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.ItemService;
import wow.minmax.service.impl.enumerators.FilterOutWorseEnchantChoices;
import wow.minmax.service.impl.enumerators.FilterOutWorseGemChoices;
import wow.minmax.service.impl.enumerators.GemComboFinder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Service("nonCachedItemService")
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemDataRepository itemDataRepository;
	private final ItemConfig itemConfig;

	private final GemComboFinder gemComboFinder = new GemComboFinder(this);

	@Override
	public Item getItem(int itemId, Phase phase) {
		return itemDataRepository.getItem(itemId, phase).orElseThrow();
	}

	@Override
	public List<Item> getItemsBySlot(PlayerProfile playerProfile, ItemSlot itemSlot) {
		return itemDataRepository.getItemsBySlot(itemSlot, playerProfile.getPhase()).stream()
				.filter(item -> playerProfile.canEquip(itemSlot, item))
				.filter(this::meetsConfigFilter)
				.filter(item -> item.isAvailableTo(playerProfile.getCharacterInfo()))
				.filter(item -> hasStatsSuitableForRole(item, playerProfile))
				.collect(Collectors.toList());
	}

	private boolean meetsConfigFilter(Item item) {
		return item.getItemLevel() >= itemConfig.getMinItemLevel() &&
				item.getRarity().isAtLeastAsGoodAs(itemConfig.getMinRarity()) &&
				!item.isPvPReward() || itemConfig.isIncludePvpItems();
	}

	@Override
	public List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType) {
		return itemDataRepository.getEnchants(itemType, playerProfile.getPhase()).stream()
				.filter(enchant -> enchant.isAvailableTo(playerProfile.getCharacterInfo()))
				.filter(enchant -> hasStatsSuitableForRole(enchant, itemType, playerProfile))
				.collect(Collectors.toList());
	}

	@Override
	public List<Enchant> getBestEnchants(PlayerProfile playerProfile, ItemType itemType) {
		List<Enchant> enchants = getEnchants(playerProfile, itemType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean nonUniqueOnly) {
		return itemDataRepository.getGems(socketType, playerProfile.getPhase()).stream()
				.filter(gem -> !nonUniqueOnly || !(gem.isUnique() || gem.isAvailableOnlyByQuests()))
				.filter(gem -> gem.isAvailableTo(playerProfile.getCharacterInfo()))
				.filter(gem -> hasStatsSuitableForRole(gem, playerProfile))
				.collect(Collectors.toList());
	}

	@Override
	public List<Gem> getBestGems(PlayerProfile playerProfile, SocketType socketType) {
		List<Gem> gems = getGems(playerProfile, socketType, true);
		if (socketType == SocketType.META) {
			return gems;
		}
		return new FilterOutWorseGemChoices(gems).getResult();
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerProfile playerProfile, Item item) {
		return gemComboFinder.getGemCombos(playerProfile, item.getSocketSpecification());
	}

	private boolean hasStatsSuitableForRole(AttributeSource attributeSource, PlayerProfile playerProfile) {
		if (playerProfile.getRole() == PveRole.CASTER_DPS) {
			return hasStatsSuitableForCasterDps(attributeSource, playerProfile);
		}
		throw new IllegalArgumentException("Unsupported role: " + playerProfile.getRole());
	}

	private boolean hasStatsSuitableForRole(Enchant enchant, ItemType itemType, PlayerProfile playerProfile) {
		if (playerProfile.getRole() == PveRole.CASTER_DPS) {
			return hasStatsSuitableForCasterDps(enchant, itemType, playerProfile);
		}
		throw new IllegalArgumentException("Unsupported role: " + playerProfile.getRole());
	}

	private boolean hasStatsSuitableForCasterDps(AttributeSource attributeSource, PlayerProfile playerProfile) {
		if (attributeSource.getHealingPower() > attributeSource.getSpellPower() && !itemConfig.isIncludeHealingItems()) {
			return false;
		}

		if (hasPrimitiveStatsSuitableForCasterDps(attributeSource, playerProfile)) {
			return true;
		}

		return hasComplexStatsSuitableForCasterDps(attributeSource, playerProfile);
	}

	private boolean hasStatsSuitableForCasterDps(Enchant enchant, ItemType itemType, PlayerProfile playerProfile) {
		if (hasStatsSuitableForRole(enchant, playerProfile)) {
			return true;
		}
		if (itemType == ItemType.WRIST) {
			return enchant.getIntellect() > 0;
		}
		if (itemType == ItemType.CHEST) {
			return enchant.getBaseStatsIncrease() > 0;
		}
		if (itemType == ItemType.BACK) {
			return enchant.getThreatReductionPct().getValue() > 0;
		}
		if (itemType == ItemType.FEET) {
			return enchant.getSpeedIncreasePct().getValue() > 0;
		}
		return false;
	}

	private static boolean hasPrimitiveStatsSuitableForCasterDps(AttributeSource attributeSource, PlayerProfile playerProfile) {
		return attributeSource.getPrimitiveAttributeList().stream()
				.anyMatch(attribute -> isCasterStat(attribute, playerProfile));
	}

	private static boolean hasComplexStatsSuitableForCasterDps(AttributeSource attributeSource, PlayerProfile playerProfile) {
		StatProvider statProvider = StatProvider.fixedValues(0.99, 0.30, playerProfile.getDamagingSpell().getCastTime());

		for (SpecialAbility specialAbility : attributeSource.getSpecialAbilities()) {
			Attributes statEquivalent = specialAbility.getStatEquivalent(statProvider);
			if (hasPrimitiveStatsSuitableForCasterDps(statEquivalent, playerProfile)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isCasterStat(PrimitiveAttribute attribute, PlayerProfile playerProfile) {
		return CASTER_STATS.contains(attribute.getId()) && hasCasterStatCondition(attribute, playerProfile);
	}

	private static final Set<PrimitiveAttributeId> CASTER_STATS = Set.of(
			SPELL_DAMAGE,
			SPELL_POWER,
			SPELL_HIT_PCT,
			SPELL_HIT_RATING,
			SPELL_CRIT_PCT,
			SPELL_CRIT_RATING,
			SPELL_HASTE_PCT,
			SPELL_HASTE_RATING
	);

	private static boolean hasCasterStatCondition(PrimitiveAttribute attribute, PlayerProfile playerProfile) {
		var damagingSpell = playerProfile.getDamagingSpell();
		var conditions = damagingSpell.getConditions(playerProfile.getActivePet(), playerProfile.getEnemyType());
		return conditions.contains(attribute.getCondition());
	}
}
