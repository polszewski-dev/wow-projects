package wow.evaluator.model;

import wow.commons.model.attribute.Attributes;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-10-13
 */
public record AttributesDiff(
		Attributes attributes,
		List<SpecialAbility> addedAbilities,
		List<SpecialAbility> removedAbilities
) {
}
