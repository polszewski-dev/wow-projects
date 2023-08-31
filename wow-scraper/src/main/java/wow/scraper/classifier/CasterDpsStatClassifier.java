package wow.scraper.classifier;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attribute.AttributeSource;
import wow.commons.model.attribute.complex.special.SpecialAbility;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.attribute.primitive.PrimitiveAttributeType;
import wow.commons.model.categorization.PveRole;

import java.util.Set;

import static wow.commons.model.attribute.primitive.PrimitiveAttributeType.*;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@AllArgsConstructor
@Component
public class CasterDpsStatClassifier implements PveRoleStatClassifier {
	@Override
	public PveRole getRole() {
		return PveRole.CASTER_DPS;
	}

	@Override
	public boolean hasStatsSuitableForRole(AttributeSource attributeSource) {
		return hasPrimitiveStatsSuitableForCasterDps(attributeSource) ||
				hasComplexStatsSuitableForCasterDps(attributeSource);
	}

	private boolean hasPrimitiveStatsSuitableForCasterDps(AttributeSource attributeSource) {
		return attributeSource.getPrimitiveAttributes().stream()
				.anyMatch(this::isCasterStat) && (attributeSource.getSpellDamage() >= attributeSource.getHealingPower());
	}

	private boolean hasComplexStatsSuitableForCasterDps(AttributeSource attributeSource) {
		return attributeSource.getSpecialAbilities().stream()
				.map(SpecialAbility::attributes)
				.anyMatch(this::hasPrimitiveStatsSuitableForCasterDps);
	}

	private boolean isCasterStat(PrimitiveAttribute attribute) {
		PrimitiveAttributeId id = attribute.id();
		return id.getPowerType().isSpellDamage() && CASTER_STATS.contains(id.getType());
	}

	private static final Set<PrimitiveAttributeType> CASTER_STATS = Set.of(
			POWER, HIT, CRIT, HASTE
	);
}
