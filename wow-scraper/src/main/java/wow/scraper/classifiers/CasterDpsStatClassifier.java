package wow.scraper.classifiers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttributeType;
import wow.commons.model.categorization.PveRole;

import java.util.Set;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeType.*;

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
