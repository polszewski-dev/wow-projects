package wow.character.service.impl.classifiers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.config.ItemConfig;
import wow.character.model.build.PveRole;
import wow.character.model.character.Character;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;

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
	public boolean hasStatsSuitableForRole(AttributeSource attributeSource, Character character) {
		if (!itemConfig.isIncludeHealingItems() && attributeSource.getHealingPower() > attributeSource.getSpellPower()) {
			return false;
		}

		if (hasPrimitiveStatsSuitableForCasterDps(attributeSource, character)) {
			return true;
		}

		return hasComplexStatsSuitableForCasterDps(attributeSource, character);
	}

	@Override
	public boolean hasStatsSuitableForRole(Enchant enchant, ItemType itemType, Character character) {
		if (hasStatsSuitableForRole(enchant, character)) {
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

	private boolean hasPrimitiveStatsSuitableForCasterDps(AttributeSource attributeSource, Character character) {
		return attributeSource.getPrimitiveAttributeList().stream()
				.anyMatch(attribute -> isCasterStat(attribute, character));
	}

	private boolean hasComplexStatsSuitableForCasterDps(AttributeSource attributeSource, Character character) {
		StatProvider statProvider = StatProvider.fixedValues(0.99, 0.30, character.getDamagingSpell().getCastTime());

		for (SpecialAbility specialAbility : attributeSource.getSpecialAbilities()) {
			Attributes statEquivalent = specialAbility.getStatEquivalent(statProvider);
			if (hasPrimitiveStatsSuitableForCasterDps(statEquivalent, character)) {
				return true;
			}
		}

		return false;
	}

	private boolean isCasterStat(PrimitiveAttribute attribute, Character character) {
		return CASTER_STATS.contains(attribute.getId()) && hasCasterStatCondition(attribute, character);
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

	private boolean hasCasterStatCondition(PrimitiveAttribute attribute, Character character) {
		var damagingSpell = character.getDamagingSpell();
		var conditions = damagingSpell.getConditions(character.getActivePet(), character.getEnemyType());
		return conditions.contains(attribute.getCondition());
	}
}
