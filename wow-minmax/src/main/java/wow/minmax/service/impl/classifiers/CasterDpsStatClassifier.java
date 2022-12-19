package wow.minmax.service.impl.classifiers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.PveRole;
import wow.commons.model.item.Enchant;
import wow.minmax.config.ItemConfig;
import wow.minmax.model.PlayerProfile;

import java.util.Set;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@AllArgsConstructor
@Component
public class CasterDpsStatClassifier implements PveRoleStatClassifier {
	private final ItemConfig itemConfig;

	@Override
	public PveRole getRole() {
		return PveRole.CASTER_DPS;
	}

	@Override
	public boolean hasStatsSuitableForRole(AttributeSource attributeSource, PlayerProfile playerProfile) {
		if (!itemConfig.isIncludeHealingItems() && attributeSource.getHealingPower() > attributeSource.getSpellPower()) {
			return false;
		}

		if (hasPrimitiveStatsSuitableForCasterDps(attributeSource, playerProfile)) {
			return true;
		}

		return hasComplexStatsSuitableForCasterDps(attributeSource, playerProfile);
	}

	@Override
	public boolean hasStatsSuitableForRole(Enchant enchant, ItemType itemType, PlayerProfile playerProfile) {
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

	private boolean hasPrimitiveStatsSuitableForCasterDps(AttributeSource attributeSource, PlayerProfile playerProfile) {
		return attributeSource.getPrimitiveAttributeList().stream()
				.anyMatch(attribute -> isCasterStat(attribute, playerProfile));
	}

	private boolean hasComplexStatsSuitableForCasterDps(AttributeSource attributeSource, PlayerProfile playerProfile) {
		StatProvider statProvider = StatProvider.fixedValues(0.99, 0.30, playerProfile.getDamagingSpell().getCastTime());

		for (SpecialAbility specialAbility : attributeSource.getSpecialAbilities()) {
			Attributes statEquivalent = specialAbility.getStatEquivalent(statProvider);
			if (hasPrimitiveStatsSuitableForCasterDps(statEquivalent, playerProfile)) {
				return true;
			}
		}

		return false;
	}

	private boolean isCasterStat(PrimitiveAttribute attribute, PlayerProfile playerProfile) {
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

	private boolean hasCasterStatCondition(PrimitiveAttribute attribute, PlayerProfile playerProfile) {
		var damagingSpell = playerProfile.getDamagingSpell();
		var conditions = damagingSpell.getConditions(playerProfile.getActivePet(), playerProfile.getEnemyType());
		return conditions.contains(attribute.getCondition());
	}
}
