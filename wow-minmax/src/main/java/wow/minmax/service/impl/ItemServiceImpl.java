package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.attributes.AttributeCondition;
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
import wow.commons.model.spells.Spell;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.config.ItemConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.ItemService;
import wow.minmax.service.impl.enumerators.FilterOutWorseEnchantChoices;
import wow.minmax.service.impl.enumerators.FilterOutWorseGemChoices;
import wow.minmax.service.impl.enumerators.GemComboFinder;

import java.util.ArrayList;
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
	public Item getItem(int itemId) {
		return itemDataRepository.getItem(itemId).orElseThrow();
	}

	@Override
	public List<Item> getItemsByType(PlayerProfile playerProfile, ItemType itemType) {
		return itemDataRepository.getItemsByType(itemType).stream()
				.filter(item -> isSuitableFor(item, playerProfile))
				.filter(item -> item.getItemLevel() >= itemConfig.getMinItemLevel())
				.filter(item -> item.getRarity().isAtLeastAsGoodAs(itemConfig.getMinRarity()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Item> getItemsBySlot(PlayerProfile playerProfile, ItemSlot itemSlot) {
		List<Item> result = new ArrayList<>();
		for (ItemType itemType : itemSlot.getItemTypes()) {
			result.addAll(getItemsByType(playerProfile, itemType));
		}
		return result;
	}

	@Override
	public List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType) {
		return itemDataRepository.getEnchants(itemType).stream()
				.filter(enchant -> isSuitableFor(enchant, itemType, playerProfile))
				.collect(Collectors.toList());
	}

	@Override
	public List<Enchant> getBestEnchants(PlayerProfile playerProfile, ItemType itemType) {
		List<Enchant> enchants = getEnchants(playerProfile, itemType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean nonUniqueOnly) {
		return itemDataRepository.getAllGems().stream()
				.filter(gem -> socketType.accepts(gem.getColor()))
				.filter(gem -> !nonUniqueOnly || !(gem.isUnique() || gem.isAvailableOnlyByQuests()))
				.filter(gem -> isSuitableFor(gem, playerProfile))
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

	private boolean isSuitableFor(Item item, PlayerProfile playerProfile) {
		if (!item.canBeEquippedBy(playerProfile.getCharacterInfo())) {
			return false;
		}
		if (item.isPvPReward() && !itemConfig.isIncludePvpItems()) {
			return false;
		}
		return hasStatsSuitableForRole(item, playerProfile);
	}

	private boolean isSuitableFor(Enchant enchant, ItemType itemType, PlayerProfile playerProfile) {
		if (!enchant.getRestriction().isMetBy(playerProfile.getCharacterInfo())) {
			return false;
		}
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

	private boolean isSuitableFor(Gem gem, PlayerProfile playerProfile) {
		if (!gem.getRestriction().isMetBy(playerProfile.getCharacterInfo())) {
			return false;
		}
		return hasStatsSuitableForRole(gem, playerProfile);
	}

	private boolean hasStatsSuitableForRole(AttributeSource attributeSource, PlayerProfile playerProfile) {
		if (playerProfile.getRole() == PveRole.CASTER_DPS) {
			return hasStatsSuitableForCasterDps(attributeSource, playerProfile);
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

		StatProvider statProvider = StatProvider.fixedValues(0.99, 0.30, playerProfile.getDamagingSpell().getCastTime());

		for (SpecialAbility specialAbility : attributeSource.getSpecialAbilities()) {
			Attributes statEquivalent = specialAbility.getStatEquivalent(statProvider);
			if (hasPrimitiveStatsSuitableForCasterDps(statEquivalent, playerProfile)) {
				return true;
			}
		}

		return false;
	}

	private static boolean hasPrimitiveStatsSuitableForCasterDps(AttributeSource attributeSource, PlayerProfile playerProfile) {
		return attributeSource.getPrimitiveAttributeList().stream()
				.anyMatch(attribute -> isCasterStat(attribute, playerProfile));
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
		Spell damagingSpell = playerProfile.getDamagingSpell();
		return Set.of(
				AttributeCondition.EMPTY,
				AttributeCondition.of(damagingSpell.getTalentTree()),
				AttributeCondition.of(damagingSpell.getSpellSchool()),
				AttributeCondition.of(damagingSpell.getSpellId()),
				AttributeCondition.of(playerProfile.getEnemyType())
			).contains(attribute.getCondition());
	}
}
